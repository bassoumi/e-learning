package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.contentcourse.ContentRequest;
import com.CRUD.firstApp.quiz.QuizRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CoursRequest(

        @NotBlank(message = "title must be valid")
        String title,

        @NotBlank(message = "description must be valid")
        String description,

        @NotBlank(message = "shortDescription must be valid")
        String shortDescription,

        @NotBlank(message = "level must be valid")
        String level,

        @NotBlank(message = "language must be valid")
        String language,

        @NotBlank(message = "coverImage must be valid")
        String coverImage,

        @NotNull
        int categoryId,

        @NotNull
        CourseMetaDataRequest metadata,

        List<ContentRequest> contents,

        int instructorId,

        QuizRequest quiz

) {}
