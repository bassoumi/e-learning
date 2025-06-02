package com.CRUD.firstApp.agenda.PersonalEvent;

import com.CRUD.firstApp.student.Student;

public class PersonalEventMapper {
    private PersonalEventMapper() { /* classe utilitaire, pas d’instanciation */ }

    /**
     * Convertit un PersonalEventRequest + Student en entité PersonalEvent prête à être persistée.
     */
    public static PersonalEvent toEntity(PersonalEventRequest req, Student student) {
        PersonalEvent entity = new PersonalEvent();
        entity.setTitle(req.title());
        entity.setDescription(req.description());
        entity.setEventDateTime(req.eventDateTime());
        entity.setStudent(student);
        return entity;
    }

    /**
     * Convertit une entité PersonalEvent en record PersonalEventResponse pour le retour au client.
     */
    public static PersonalEventResponse toResponse(PersonalEvent event) {
        return new PersonalEventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDateTime(),
                event.getStudent().getId()
        );
    }
}
