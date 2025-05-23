package com.CRUD.firstApp.Categorie;

import com.CRUD.firstApp.courses.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;

    public CategorieService(CategorieRepository categorieRepository, CategorieMapper categorieMapper) {
        this.categorieRepository = categorieRepository;
        this.categorieMapper = categorieMapper;
    }

    public List<CategorieResponce> getAllCategories() {
        return categorieRepository.findAll()
                .stream()
                .map(categorieMapper::toCategorieResponce)
                .collect(Collectors.toList());

    }

    public CategorieResponce getCategorieById(int id ) {
        var category =  categorieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id));
        return categorieMapper.toCategorieResponce(category);
    }


    public Categorie getEntityById(int id) {
        return categorieRepository.findById(id)
                .orElseThrow(() ->     new ResourceNotFoundException("Catégorie introuvable pour l'id " + id)
                );
    }


    public CategorieResponce createCategorie(@Valid CategorieRequest request) {
        var categorie = categorieMapper.toCategorie(request);
        categorieRepository.save(categorie);
        var categoryResponse = categorieMapper.toCategorieResponce(categorie);
        return categoryResponse;
    }

    public List<CategorieResponce> getCategorieByName(String name) {
       return categorieRepository.findByNom(name).stream()
                .map(categorieMapper::toCategorieResponce)
               .collect(Collectors.toList());

    }

    public CategorieResponce updateCategroie(int id, CategorieRequest request) {
        Categorie category =  categorieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id));

        if (StringUtils.hasText(request.nom())) {
            category.setNom(request.nom().trim());
        }

        if (StringUtils.hasText(request.description())) {
            category.setDescription(request.description().trim());
        }

        if (StringUtils.hasText(request.slug())) {
            category.setSlug(request.slug().trim());
        }

        if (StringUtils.hasText(request.CoverCategoryimage())) {
            category.setCoverCategoryimage(request.CoverCategoryimage().trim());
        }

        Categorie updated = categorieRepository.save(category);

        return categorieMapper.toCategorieResponce(updated);

    }

    public void deleteById(int id) {
        categorieRepository.deleteById(id);
    }
}
