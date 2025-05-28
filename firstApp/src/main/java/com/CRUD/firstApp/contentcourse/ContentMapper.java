package com.CRUD.firstApp.contentcourse;


import com.CRUD.firstApp.courses.Courses;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public Content toContent(ContentRequest request, Courses course) {
        Content content = new Content();
        content.setCourse(course); // ✅ use the actual object
        content.setTitle(request.title());
        content.setDescription(request.description());
        content.setVideoUrl(request.videoUrl());
        content.setOrderContent(request.orderContent());
        return content;
    }

    public ContentResponce toResponce(Content content) {
        return new ContentResponce(
                content.getId(),
                String.valueOf(content.getCourse().getId()), // convert int to String if needed
                content.getTitle(),
                content.getDescription(),
                content.getVideoUrl(),
                content.getOrderContent()
        );
    }
}
