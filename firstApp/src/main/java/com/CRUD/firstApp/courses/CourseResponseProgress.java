package com.CRUD.firstApp.courses;

public record CourseResponseProgress(
        int id,
        String title,
        String shortDescription,
        String level,
        String language,
        String coverImage,
        String categoryName,
        Integer categoryId,
        Integer instructorId,
        String instructorFirstName,
        String lastContentTitle,
        Integer lastContentId,
        Double progressionPercentage
) {

}
