package com.CRUD.firstApp.courses;

import com.CRUD.firstApp.contentcourse.ContentResponce;
import com.CRUD.firstApp.quiz.QuizResponse;

import java.util.List;

public record CourseResponse(
        int id,
        String title,
        String description,
        String shortDescription,
        String level,
        String language,
        String coverImage,
        String categoryName,
        Integer categoryId,      // ← passage à Integer
        Integer instructorId,    // ← passage à Integer
        CourseMetaData courseMetaData,
        String instructorNames,
        List<ContentResponce> contents,
        QuizResponse quiz,
        String instructorProfileImage
) {}

