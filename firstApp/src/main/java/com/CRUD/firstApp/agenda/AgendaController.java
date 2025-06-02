package com.CRUD.firstApp.agenda;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }


    @GetMapping("/{studentId}/agenda")
    public ResponseEntity<List<AgendaEntryDto>> getAgendaEntries(
            @PathVariable Integer studentId) {

        List<AgendaEntryDto> dtos = agendaService.getAgendaEntriesForStudent(studentId);
        return ResponseEntity.ok(dtos);
    }
}
