package com.CRUD.firstApp.Categorie;

import com.CRUD.firstApp.courses.CourseFileStorageService;
import com.CRUD.firstApp.courses.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;
    private final CourseFileStorageService courseFileStorageService;

    public CategorieService(CategorieRepository categorieRepository, CategorieMapper categorieMapper, CourseFileStorageService courseFileStorageService) {
        this.categorieRepository = categorieRepository;
        this.categorieMapper = categorieMapper;
        this.courseFileStorageService = courseFileStorageService;
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


    public CategorieResponce createCategorie(
            String nom,
            String description,
            MultipartFile coverImage
    ) throws IOException {
        // 1. Sauvegarde du fichier sur disque (même dossier que pour les instructeurs)
        String storedName = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String originalName = coverImage.getOriginalFilename();
            storedName = UUID.randomUUID() + "_" + originalName;
            Path target = Paths.get("uploads/images", storedName);
            Files.createDirectories(target.getParent());
            Files.write(target, coverImage.getBytes());
        }

        // 2. Création de l’entité
        Categorie categorie = new Categorie();
        categorie.setNom(nom);
        categorie.setDescription(description);
        categorie.setCreationDate(LocalDateTime.now());
        categorie.setModificationDate(LocalDateTime.now());
        // on stocke le nom du fichier comme pour Instructor
        categorie.setCoverCategoryimage(storedName);

        // 3. Persistance
        Categorie saved = categorieRepository.save(categorie);

        // 4. Conversion en DTO (le mapper doit gérer exactement comme pour Instructor)
        return categorieMapper.toCategorieResponce(saved);
    }




    public List<CategorieResponce> getCategorieByName(String name) {
       return categorieRepository.findByNom(name).stream()
                .map(categorieMapper::toCategorieResponce)
               .collect(Collectors.toList());

    }

    public CategorieResponce updateCategorie(
            Integer id,
            String nom,
            String description,
            MultipartFile coverImage
    ) throws IOException {
        // 1. On récupère l’entité existante
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + id
                ));

        // 2. Mise à jour des textes
        if (StringUtils.hasText(nom)) {
            categorie.setNom(nom.trim());
        }
        if (StringUtils.hasText(description)) {
            categorie.setDescription(description.trim());
        }
        categorie.setModificationDate(LocalDateTime.now());

        // 3. Même logique de stockage que dans createCategorie
        if (coverImage != null && !coverImage.isEmpty()) {
            String originalName = coverImage.getOriginalFilename();
            String storedName   = UUID.randomUUID() + "_" + originalName;
            Path target         = Paths.get("uploads/images", storedName);
            Files.createDirectories(target.getParent());
            Files.write(target, coverImage.getBytes());
            categorie.setCoverCategoryimage(storedName);
        }

        // 4. Persistance et retour DTO
        Categorie updated = categorieRepository.save(categorie);
        return categorieMapper.toCategorieResponce(updated);
    }


    public void deleteById(int id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorie not found"));

        if (!categorie.getCourses().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing courses.");
        }

        categorieRepository.deleteById(id);
    }

}
