# chatbot/actions/actions.py

from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests
from datetime import datetime, timedelta


class ActionListNewCourses(Action):
    def name(self) -> Text:
        return "action_list_new_courses"

    def run(
            self,
            dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any],
    ) -> List[Dict[Text, Any]]:
        """
        Fetches all courses created this week from the API.
        No level filtering is applied. The user only wants to see *new* courses.
        """

        try:
            response = requests.get("http://host.docker.internal:8080/api/v1/courses")
            response.raise_for_status()
            courses_data = response.json()
        except Exception:
            dispatcher.utter_message(
                text="I’m sorry, but I’m unable to retrieve the courses at the moment. Please try again later."
            )
            return []

        if not courses_data:
            dispatcher.utter_message(text="Currently, there are no courses available.")
            return []

        # Calculate the start of the current week (Monday 00:00)
        now = datetime.now()
        start_of_week = (now - timedelta(days=now.weekday())).replace(
            hour=0, minute=0, second=0, microsecond=0
        )

        # Filter courses created this week
        new_courses = []
        for course in courses_data:
            metadata = course.get("courseMetaData", {})
            created_at_str = metadata.get("createdAt")
            if not created_at_str:
                continue

            try:
                created_at = datetime.fromisoformat(created_at_str)
            except ValueError:
                continue

            if created_at >= start_of_week:
                new_courses.append(course)

        if not new_courses:
            dispatcher.utter_message(
                text="No new courses have been created this week."
            )
            return []

        # Build a professional message
        total = len(new_courses)
        message = f"This week, {total} new course{'s' if total > 1 else ''} have been created:\n"
        for course in new_courses:
            title = course.get("title", "Untitled Course")
            category = course.get("categoryName", "Unspecified Category")
            instructor = course.get("instructorNames", "Unspecified Instructor")
            message += f"• **{title}** (Category: {category} | Instructor: {instructor})\n"

        dispatcher.utter_message(text=message.strip())
        return []


class ActionListCoursesByLevel(Action):
    def name(self) -> Text:
        return "action_list_courses_by_level"

    def run(
            self,
            dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any],
    ) -> List[Dict[Text, Any]]:
        """
        Fetches all available courses and filters them by the level specified by the user.
        Does not care if the course is new or not.
        """

        level_filter = tracker.get_slot("level")
        if not level_filter:
            dispatcher.utter_message(
                text="Please specify a level (beginner, intermediate, advanced) so I can list relevant courses."
            )
            return []

        try:
            response = requests.get("http://host.docker.internal:8080/api/v1/courses")
            response.raise_for_status()
            courses_data = response.json()
        except Exception:
            dispatcher.utter_message(
                text="I’m sorry, but I cannot retrieve the courses at the moment. Please try again later."
            )
            return []

        if not courses_data:
            dispatcher.utter_message(text="Currently, there are no courses available.")
            return []

        # Filter courses by level
        level = level_filter.lower()
        matched_courses = [
            course for course in courses_data
            if course.get("level", "").lower() == level
        ]

        if not matched_courses:
            dispatcher.utter_message(
                text=f"No {level} courses are available at the moment."
            )
            return []

        total = len(matched_courses)
        message = f"We have {total} {level} course{'s' if total > 1 else ''} available:\n"
        for course in matched_courses:
            title = course.get("title", "Untitled Course")
            category = course.get("categoryName", "Unspecified Category")
            instructor = course.get("instructorNames", "Unspecified Instructor")
            message += f"• **{title}** (Category: {category} | Instructor: {instructor})\n"

        dispatcher.utter_message(text=message.strip())
        return []
