package com.CRUD.firstApp.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AgendaEntryDto(
        Integer id,
        LocalDate studyDate,
        LocalDateTime startedAt,
        LocalDateTime lastProgressionUpdate,
        String eventType,
        Integer courseId,
        String courseTitle,
        String instructorName,
        String coursImage

) {
}
