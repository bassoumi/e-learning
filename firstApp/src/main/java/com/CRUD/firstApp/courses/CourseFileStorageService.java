package com.CRUD.firstApp.courses;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CourseFileStorageService {
    private final Path uploadDir = Paths.get("uploads/courses");
    private final Path categoryUploadDir = Paths.get("uploads/images");

    public CourseFileStorageService() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        if (!Files.exists(categoryUploadDir)) {
            Files.createDirectories(categoryUploadDir);
        }
    }

    public String storeFile(MultipartFile file) {
        // Nettoyage du nom d’origine
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        // Générer un nom unique (timestamp + nom d’origine)
        String filename = System.currentTimeMillis() + "_" + originalFilename;

        try {
            // Sécurité : refuser les chemins relatifs
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Nom de fichier invalide : " + originalFilename);
            }

            // Copier le contenu du fichier dans le dossier cible
            Path targetLocation = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de l’enregistrement du fichier " + originalFilename, ex);
        }
    }

    public String storeCategoryFile(MultipartFile file) {
        // Nettoyage du nom d’origine
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        // Générer un nom unique (timestamp + nom d’origine)
        String filename = System.currentTimeMillis() + "_" + originalFilename;

        try {
            // Sécurité : refuser les chemins relatifs
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Nom de fichier invalide : " + originalFilename);
            }

            // Copier le contenu du fichier dans le dossier cible
            Path targetLocation = categoryUploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de l’enregistrement du fichier " + originalFilename, ex);
        }
    }
}
