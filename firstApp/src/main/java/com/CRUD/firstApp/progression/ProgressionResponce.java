package com.CRUD.firstApp.progression;

import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.student.Student;

import java.time.LocalDateTime;

public record ProgressionResponce(
        Integer id,
        Integer studentId,
        Integer contentId,
        Double progressionPercentage,
        LocalDateTime lastAccessed,
        ProgressionStatus status
) {
}
