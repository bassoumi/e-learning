package com.CRUD.firstApp.agenda.PersonalEvent;

import java.time.LocalDateTime;

public record PersonalEventResponse (
        Integer id,
        String title,
        String description,
        LocalDateTime eventDateTime,
        Integer studentId
) {
}
