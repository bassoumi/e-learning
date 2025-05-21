package com.CRUD.firstApp.Categorie;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public CategorieResponce createCategorie(@Valid @RequestBody CategorieRequest request) {
        return categorieService.createCategorie(request);
    }

    @PutMapping("/{id}")
    public CategorieResponce updateCategorie( @PathVariable int id , @RequestBody CategorieRequest request) {
        return categorieService.updateCategroie(id ,request );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategorie(@PathVariable int id) {
         categorieService.deleteById(id);
        return new ResponseEntity<>("Categorie deleted", HttpStatus.OK);
    }


}
