package com.CRUD.firstApp.Categorie;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CategorieResponce(



        String nom,

        String description,

        String slug,

        String CoverCategoryimage



) {
}
