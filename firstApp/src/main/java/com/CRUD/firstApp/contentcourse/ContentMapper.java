package com.CRUD.firstApp.contentcourse;


import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public Content toContent(ContentRequest request) {
        Content content = new Content();
        content.setTitle(request.title());
        content.setDescription(request.description());
        content.setVideoUrl(request.videoUrl());
        content.setOrderContent(request.orderContent());
        return content;
    }

    public ContentResponce toResponce(Content content) {
        return  new ContentResponce(
                content.getTitle(),
                content.getDescription(),
                content.getVideoUrl(),
                content.getOrderContent()
        );
    }
}
