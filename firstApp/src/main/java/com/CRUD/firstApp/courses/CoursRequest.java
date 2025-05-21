package com.CRUD.firstApp.courses;


import jakarta.validation.constraints.NotBlank;


public record CoursRequest(

         @NotBlank(message = "title must be valid ")
         String title,

         @NotBlank(message = "description must be valid ")
         String description,

         @NotBlank(message = "shortDescription must be valid ")
         String shortDescription,

         @NotBlank(message = "level must be valid ")
         String level,

         @NotBlank(message = "language must be valid ")
         String language,

         @NotBlank(message = "coverImage must be valid ")
         String coverImage,

         CourseMetaDataRequest metadata


) {
}
