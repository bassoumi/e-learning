package com.CRUD.firstApp.courses;

import jakarta.validation.constraints.NotBlank;

public record CourseResponce(
        String title,

        String description,

        String shortDescription,

        String level,

        String language,

        String coverImage

) {
}
