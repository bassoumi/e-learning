package com.CRUD.firstApp.agenda.PersonalEvent;


import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalEventService {

    private final PersonalEventRepository eventRepository;
    private final StudentRepository studentRepository;

    public PersonalEventService(PersonalEventRepository eventRepository,
                                StudentRepository studentRepository) {
        this.eventRepository = eventRepository;
        this.studentRepository = studentRepository;
    }

    public PersonalEventResponse createEvent(Integer studentId, PersonalEventRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable (ID: " + studentId + ")"
                ));

        // Utilisation du mapper pour construire l'entité
        PersonalEvent eventEntity = PersonalEventMapper.toEntity(req, student);
        PersonalEvent saved = eventRepository.save(eventEntity);

        // Retourne le DTO via le mapper
        return PersonalEventMapper.toResponse(saved);
    }

    public void deleteEvent(Integer studentId, Integer eventId) {
        PersonalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Événement introuvable (ID: " + eventId + ")"
                ));

        if (event.getStudent().getId() != studentId) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous n'avez pas le droit de supprimer cet événement"
            );
        }


        eventRepository.delete(event);
    }

    public List<PersonalEventResponse> getAllEventsForStudent(Integer studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable (ID: " + studentId + ")"
                ));

        return eventRepository.findAllByStudentId(studentId).stream()
                .map(PersonalEventMapper::toResponse)
                .collect(Collectors.toList());
    }
}
