package com.CRUD.firstApp.Categorie;

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


    public CategorieResponce createCategorie(
            String nom,
            String description,
            MultipartFile coverImage
    ) throws IOException {
        // 1. Créer l’entité
        Categorie categorie = new Categorie();
        categorie.setNom(nom);
        categorie.setDescription(description);
        categorie.setCreationDate(LocalDateTime.now());
        categorie.setModificationDate(LocalDateTime.now());

        // 2. Sauvegarde de l’image « à la volée »
        if (coverImage != null && !coverImage.isEmpty()) {
            String originalName = StringUtils.cleanPath(coverImage.getOriginalFilename());

            // 🔐 Nettoyage du nom pour enlever les caractères spéciaux
            String safeName = Normalizer.normalize(originalName, Normalizer.Form.NFD)
                    .replaceAll("[^\\w\\.-]", "_"); // autorise lettres, chiffres, . et -

            String storedName = UUID.randomUUID() + "_" + safeName;

            // 📂 Dossier de stockage
            Path uploadDir = Paths.get("uploads/categories");
            Files.createDirectories(uploadDir); // crée si non existant

            Path target = uploadDir.resolve(storedName).normalize();
            Files.write(target, coverImage.getBytes());

            categorie.setCoverCategoryimage(storedName);
        } else {
            categorie.setCoverCategoryimage(null);
        }

        // 3. Sauvegarde en base
        Categorie saved = categorieRepository.save(categorie);

        // 4. Retour DTO
        return categorieMapper.toCategorieResponce(saved);
    }

    public List<CategorieResponce> getCategorieByName(String name) {
       return categorieRepository.findByNom(name).stream()
                .map(categorieMapper::toCategorieResponce)
               .collect(Collectors.toList());

    }

    public CategorieResponce updateCategroie(int id, CategorieRequest request) throws IOException {
        Categorie category = categorieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id));

        // Mise à jour des champs textuels s'ils sont présents
        if (StringUtils.hasText(request.nom())) {
            category.setNom(request.nom().trim());
        }

        if (StringUtils.hasText(request.description())) {
            category.setDescription(request.description().trim());
        }

        category.setModificationDate(LocalDateTime.now());

        // Mise à jour de l'image si une nouvelle image est fournie
        if (request.CoverCategoryimage() != null && !request.CoverCategoryimage().isEmpty()) {
            String originalName = StringUtils.cleanPath(request.CoverCategoryimage().getOriginalFilename());

            // Nettoyage du nom pour éviter les erreurs liées aux caractères spéciaux
            String safeName = Normalizer.normalize(originalName, Normalizer.Form.NFD)
                    .replaceAll("[^\\w\\.-]", "_"); // Remplace les caractères non valides

            String storedName = UUID.randomUUID() + "_" + safeName;

            Path uploadDir = Paths.get("uploads/categories");
            Files.createDirectories(uploadDir); // Crée le dossier si nécessaire

            Path target = uploadDir.resolve(storedName).normalize();
            Files.write(target, request.CoverCategoryimage().getBytes());

            category.setCoverCategoryimage(storedName);
        }

        // Sauvegarde finale
        Categorie updated = categorieRepository.save(category);
        return categorieMapper.toCategorieResponce(updated);
    }

    public void deleteById(int id) {
        categorieRepository.deleteById(id);
    }
}
