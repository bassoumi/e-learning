package com.CRUD.firstApp.Categorie;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")

public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService CategorieService) {
        this.categorieService = CategorieService;
    }

    @GetMapping
    public List<CategorieResponce> getAllCategories() {
        return categorieService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategorieResponce getCategorieById(@PathVariable int id ) {
        return categorieService.getCategorieById(id);
    }

    @GetMapping("name")
    public List<CategorieResponce> getCategorieByName(@RequestParam String name ) {
        return categorieService.getCategorieByName(name);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategorieResponce createCategorie(
            @RequestParam("nom") @NotBlank String nom,
            @RequestParam("description") @NotBlank String description,
            @RequestPart("CoverCategoryimage") @NotNull MultipartFile coverImage
    ) throws IOException {
        return categorieService.createCategorie(nom, description, coverImage);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategorieResponce updateCategorie(
            @PathVariable int id,

            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "covercategoryimage", required = false) MultipartFile coverImage
    ) throws IOException {

        // Build the request DTO with nulls for fields not sent
        CategorieRequest request = new CategorieRequest(
                nom,
                description,
                coverImage,
                null,
                null
        );

        return categorieService.updateCategroie(id, request);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategorie(@PathVariable int id) {
         categorieService.deleteById(id);
        return new ResponseEntity<>("Categorie deleted", HttpStatus.OK);
    }


}
