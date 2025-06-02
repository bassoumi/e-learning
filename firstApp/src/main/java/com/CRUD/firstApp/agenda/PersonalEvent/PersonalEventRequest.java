package com.CRUD.firstApp.agenda.PersonalEvent;

import java.time.LocalDateTime;

public record PersonalEventRequest(
        String title,
        String description,      // peut être null
        LocalDateTime eventDateTime
) {
}
