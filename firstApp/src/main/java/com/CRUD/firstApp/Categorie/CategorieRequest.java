package com.CRUD.firstApp.Categorie;


import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public record CategorieRequest(


        @NotBlank(message = "nom cannot be Empty ")
         String nom,

        @NotBlank(message = "description cannot be Empty ")
        String description,

        @NotBlank(message = "slug cannot be Empty ")
        String slug,

        @NotBlank(message = "CoverCategoryimage cannot be Empty ")
        String CoverCategoryimage,


         LocalDateTime creationDate,

         LocalDateTime modificationDate

) {
}
