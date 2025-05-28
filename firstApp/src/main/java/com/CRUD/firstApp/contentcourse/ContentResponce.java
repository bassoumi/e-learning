package com.CRUD.firstApp.contentcourse;

public record ContentResponce(
        int id,
        String courseId,
        String title,
        String description,
        String videoUrl,
        Integer orderContent
) {
}
