package com.CRUD.firstApp.courses;

import com.CRUD.firstApp.contentcourse.ContentResponce;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CourseResponce(
        String title,
        String description,
        String shortDescription,
        String level,
        String language,
        String coverImage,
        String categoryName,
        List<String> instructorNames,
        List<ContentResponce> contents
) {}
