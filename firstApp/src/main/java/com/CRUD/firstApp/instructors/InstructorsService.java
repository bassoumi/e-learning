package com.CRUD.firstApp.instructors;


import com.CRUD.firstApp.courses.ResourceNotFoundException;
import org.apache.catalina.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstructorsService {

    private final InstructorsRepository instructorsRepository;
    private final InstructorsMapper instructorsMapper;

    public InstructorsService(InstructorsRepository instructorsRepository, InstructorsMapper instructorsMapper) {
        this.instructorsRepository = instructorsRepository;
        this.instructorsMapper = instructorsMapper;
    }



    public List<InstructorsResponce> getAllInstructors() {
        return instructorsRepository.findAll()
                .stream()
                .map(instructorsMapper::toResponce)
                .collect(Collectors.toList());
    }

    public Instructors getInstructorById(int id) {
        return   instructorsRepository.findById(id)
                .orElseThrow(()->   new ResourceNotFoundException("Instructeur introuvable pour l'id " + id));


    }



    public List<InstructorsResponce> getInstructorsByName(String name) {
        var IntrucorsExsistng = instructorsRepository.findByFirstName(name);
        if (IntrucorsExsistng.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Instructor not found with name "+ name);
        }
        return IntrucorsExsistng.stream()
                .map(instructorsMapper::toResponce)
                .collect(Collectors.toList());
    }

    public InstructorsResponce addInstructor(InstructorsRequest request) throws IOException {
        // 1. Sauvegarde du fichier sur disque (ou cloud)
        String originalName = request.profileImage().getOriginalFilename();
        String storedName   = UUID.randomUUID() + "_" + originalName;
        Path target         = Paths.get("uploads/images", storedName);
        Files.createDirectories(target.getParent());
        Files.write(target, request.profileImage().getBytes());

        // 2. Transformation en entité
        var entity = instructorsMapper.toInstructors(request, storedName);
        instructorsRepository.save(entity);

        return instructorsMapper.toResponce(entity);
    }


    public InstructorsResponce updateInstructor(int id, InstructorsRequest request) throws IOException {
        // 1) Fetch existing
        Instructors existing = instructorsRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found with id " + id)
                );

        // 2) If new image uploaded → store it & update field
        MultipartFile newImage = request.profileImage();
        if (newImage != null && !newImage.isEmpty()) {
            // save file
            String original = newImage.getOriginalFilename();
            String stored   = UUID.randomUUID() + "_" + original;
            Path target     = Paths.get("uploads/images", stored);
            Files.createDirectories(target.getParent());
            Files.write(target, newImage.getBytes());

            existing.setProfileImage(stored);
        }

        // 3) Partial update of other fields
        if (StringUtils.hasText(request.firstName())) {
            existing.setFirstName(request.firstName());
        }
        if (StringUtils.hasText(request.lastName())) {
            existing.setLastName(request.lastName());
        }
        if (StringUtils.hasText(request.email())) {
            existing.setEmail(request.email());
        }
        if (StringUtils.hasText(request.bio())) {
            existing.setBio(request.bio());
        }
        if (StringUtils.hasText(request.password())) {
            existing.setPassword(request.password());
        }

        // 4) Persist & return
        instructorsRepository.save(existing);
        return instructorsMapper.toResponce(existing);
    }


    public void deleteInstructor(int id) {
         instructorsRepository.deleteById(id);
    }

    public Instructors getInstructorsByEmail(String email) {
        return instructorsRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Instructor not found with email " + email)
                );
    }

}

