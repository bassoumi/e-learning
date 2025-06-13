package com.CRUD.firstApp.Categorie;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record CategorieRequest(


        @NotBlank(message = "nom cannot be Empty ")
         String nom,

        @NotBlank(message = "description cannot be Empty ")
        String description,



        @NotNull(message = "CoverCategoryimage cannot be Empty")
        MultipartFile CoverCategoryimage,


         LocalDateTime creationDate,

         LocalDateTime modificationDate

) {
}
