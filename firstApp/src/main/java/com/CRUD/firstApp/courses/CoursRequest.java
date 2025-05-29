package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.contentcourse.ContentRequest;
import com.CRUD.firstApp.quiz.QuizRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.boot.Metadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public record CoursRequest(
        String             title,
        String             description,
        String             shortDescription,
        Integer            categoryId,
        String             level,
        String             language,
        Integer            instructorId,
        List<ContentRequest> contents,
        QuizRequest        quiz,
        MultipartFile      coverImage,
        CourseMetaDataRequest            metadata
) {}

