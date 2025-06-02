package com.CRUD.firstApp.agenda.PersonalEvent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students/{studentId}/events")
public class PersonalEventController {
    private final PersonalEventService eventService;

    public PersonalEventController(PersonalEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<PersonalEventResponse> createEvent(
            @PathVariable Integer studentId,
            @RequestBody PersonalEventRequest request
    ) {
        PersonalEventResponse response = eventService.createEvent(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Integer studentId,
            @PathVariable Integer eventId
    ) {
        eventService.deleteEvent(studentId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PersonalEventResponse>> listEvents(
            @PathVariable Integer studentId
    ) {
        List<PersonalEventResponse> events = eventService.getAllEventsForStudent(studentId);
        return ResponseEntity.ok(events);
    }

}
