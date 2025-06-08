# chatbot/actions/actions.py

from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests

class ActionListCourses(Action):
    def name(self) -> Text:
        return "action_list_courses"

    def run(
        self,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: Dict[Text, Any]
    ) -> List[Dict[Text, Any]]:
        """
        Cette action interroge votre API Spring Boot pour récupérer
        la liste des cours, puis renvoie un message dynamique.
        """

        try:
            # Si votre Spring Boot tourne en local (hors Docker),
            # utilisez host.docker.internal:8080
            response = requests.get("http://host.docker.internal:8080/api/v1/courses")
            response.raise_for_status()
            courses_data = response.json()
        except Exception:
            dispatcher.utter_message(text="Désolé, je ne parviens pas à accéder aux cours pour le moment.")
            return []

        if not courses_data:
            dispatcher.utter_message(text="Aucun cours n'est disponible pour l'instant.")
            return []

        total = len(courses_data)
        titles = [c.get("title", "Cours sans titre") for c in courses_data]
        message = f"Nous avons actuellement {total} cours disponibles :\n"
        for t in titles:
            message += f"- {t}\n"

        dispatcher.utter_message(text=message.strip())
        return []
