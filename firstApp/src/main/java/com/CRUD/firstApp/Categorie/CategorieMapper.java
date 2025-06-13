package com.CRUD.firstApp.Categorie;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
public class CategorieMapper {

    public Categorie toCategorie(CategorieRequest request) {
        Categorie categorie = new Categorie();
        categorie.setNom(request.nom());
        categorie.setDescription(request.description());

        MultipartFile file = request.CoverCategoryimage();
        if (file != null && !file.isEmpty()) {
            // Nettoie le nom et ajoute un UUID pour éviter les collision
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "_" + original;
            categorie.setCoverCategoryimage(filename);
        } else {
            categorie.setCoverCategoryimage(null);
        }

        return categorie;
    }

    public CategorieResponce toCategorieResponce(Categorie categorie) {
        return new CategorieResponce(
                categorie.getId(),
                categorie.getNom(),
                categorie.getDescription(),
                categorie.getCoverCategoryimage()
        );

    }

}
