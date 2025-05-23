package com.CRUD.firstApp.courses;

import com.CRUD.firstApp.contentcourse.ContentResponce;
import com.CRUD.firstApp.quiz.QuizResponse;

import java.util.List;

public record CourseResponse(
        String title,
        String description,
        String shortDescription,
        String level,
        String language,
        String coverImage,
        String categoryName,
        List<String> instructorNames,
        List<ContentResponce> contents,
        QuizResponse quiz
) {}
