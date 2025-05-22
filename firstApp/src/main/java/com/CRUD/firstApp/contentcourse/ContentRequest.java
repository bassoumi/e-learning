package com.CRUD.firstApp.contentcourse;

import jakarta.validation.constraints.NotBlank;

public record ContentRequest (
        @NotBlank(message = "title cannot be empty ")
         String title,

        @NotBlank(message = "description cannot be empty ")
        String description,

        @NotBlank(message = "videoUrl cannot be empty ")
        String videoUrl,


         Integer orderContent
){
}
