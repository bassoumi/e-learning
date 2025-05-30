package com.CRUD.firstApp.progression;

import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public record ProgressionRequest(

        Double progressionPercentage,
        LocalDateTime lastAccessed,
        ProgressionStatus status
) {
}
