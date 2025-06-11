from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests
from datetime import datetime, timedelta
import re

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
            short_desc = course.get("shortDescription", "No description available.")
            message += (
                f"**{title}**, in the “{category}” category, taught by the renowned {instructor}, "
                f"    This course is described as: {short_desc}\n"
            )

        dispatcher.utter_message(text=message.strip())
        return []



class ActionListCoursesByLevel(Action):
    def name(self) -> Text:
        return "action_list_courses_by_level"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        level_entities = list(tracker.get_latest_entity_values("level"))
        level_filter = level_entities[0] if level_entities else None

        print("Latest message:", tracker.latest_message.get("text"))
        print("All entities:", tracker.latest_message.get("entities"))
        print("DEBUG - Slot 'level' via get_latest_entity_values:", level_filter)

        if not level_filter:
            text = tracker.latest_message.get("text", "").lower()
            match = re.search(r"\b(beginner|intermediate|advanced)\b", text)
            if match:
                level_filter = match.group(1)
                print(f"DEBUG - Extracted level from text manually: {level_filter}")
            else:
                print("DEBUG - No level found manually either.")

        if not level_filter:
            dispatcher.utter_message(text="Please tell me which level you’d like: beginner, intermediate or advanced.")
            return []

        try:
            response = requests.get("http://host.docker.internal:8080/api/v1/courses")
            if response.status_code != 200:
                dispatcher.utter_message(text="I couldn't retrieve the courses at the moment.")
                print("DEBUG - API error:", response.status_code, response.text)
                return []
            courses_data = response.json()
        except Exception as e:
            print("DEBUG - Exception during API call:", e)
            dispatcher.utter_message(text="Failed to connect to the course service.")
            return []

        wanted = level_filter.lower()
        matched = [c for c in courses_data if c.get("level", "").lower() == wanted]

        if not matched:
            dispatcher.utter_message(text=f"No {wanted} courses are available at the moment.")
            return []

        lines = [f"We have {len(matched)} {wanted} course{'s' if len(matched) > 1 else ''} available:"]
        for c in matched:
            lines.append(
                f" **{c.get('title')}**, in the “{c.get('categoryName')}” category, taught by {c.get('instructorNames')}, "
                f"is described as: {c.get('shortDescription', 'No description available.')}"
            )


        dispatcher.utter_message(text="\n".join(lines))
        return []