package com.CRUD.firstApp.Categorie;


import org.springframework.stereotype.Component;

@Component
public class CategorieMapper {

    public Categorie toCategorie(CategorieRequest request) {
        Categorie categorie = new Categorie();
        categorie.setNom(request.nom());
        categorie.setDescription(request.description());
        categorie.setSlug(request.slug());
        categorie.setCoverCategoryimage(request.CoverCategoryimage());
        return categorie;
    }

    public CategorieResponce toCategorieResponce(Categorie categorie) {
        return new CategorieResponce(
                categorie.getNom(),
                categorie.getDescription(),
                categorie.getSlug(),
                categorie.getCoverCategoryimage()
        );

    }

}
