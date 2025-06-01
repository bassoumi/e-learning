package com.CRUD.firstApp.student;

import java.time.LocalDateTime;

public record NotificationDto (
        Integer id,
        Integer courseId,
        String courseTitle,
        String shortDescription,
        String level,
        String language,
        String instructorNames,
        Integer instructorId,
        String categoryName,
        Integer categoryId,
        String coverImage,
        LocalDateTime createdAt
) {
}
