package com.CRUD.firstApp.instructors;


import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentRepository;
import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.courses.ResourceNotFoundException;
import com.CRUD.firstApp.progression.ProgressionRepository;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ContentRepository  contentRepository;
    private final EntityManager entityManager;

    public InstructorsService(InstructorsRepository instructorsRepository, InstructorsMapper instructorsMapper, ContentRepository contentRepository, EntityManager EntityManager, EntityManager entityManager) {
        this.instructorsRepository = instructorsRepository;
        this.instructorsMapper = instructorsMapper;
        this.contentRepository = contentRepository;
        this.entityManager = entityManager;
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


    public InstructorsResponce getInstructorByIdForinstructors(int id) {
        Instructors instr = instructorsRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Instructeur introuvable pour l'id " + id));

        // Map the entity to your response record
        return instructorsMapper.toResponce(instr);
    }

    @Transactional
    public void deleteInstructorById(int id) {


        // 1. Charger l’instructeur
        Instructors instructor = instructorsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found with id " + id));

        // 2. Pour chaque cours de l’instructeur
        for (Courses course : instructor.getCourses()) {
            // a. Supprimer les jointures course-étudiants
            for (Student student : course.getStudents()) {
                student.getCourses().remove(course);
            }
            course.getStudents().clear();

            entityManager.createQuery("DELETE FROM Agenda a WHERE a.course.id = :courseId")
                    .setParameter("courseId", course.getId())
                    .executeUpdate();

            
            // b. Supprimer les jointures course-instructors (si bidirectionnelle)
            entityManager.createNativeQuery("DELETE FROM instructor_courses WHERE course_id = :courseId")
                    .setParameter("courseId", course.getId())
                    .executeUpdate();

            // c. Supprimer les contenus liés à ce cours
            List<Content> contents = contentRepository.findByCourseId(course.getId()); // à implémenter

            for (Content content : contents) {
                // i. Supprimer les progressions liées à ce contenu
                entityManager.createQuery("DELETE FROM Progression p WHERE p.contentEnCours.id = :contentId")
                        .setParameter("contentId", content.getId())
                        .executeUpdate();

                // ii. Supprimer le contenu
                entityManager.remove(entityManager.contains(content) ? content : entityManager.merge(content));
            }
        }

        // 3. Supprimer les cours de l’instructeur (Cascade.ALL → supprime aussi les cours)
        for (Courses course : instructor.getCourses()) {
            entityManager.remove(entityManager.contains(course) ? course : entityManager.merge(course));
        }
        instructor.getCourses().clear();

        // 4. Supprimer les jointures instructeur-étudiants
        for (Student student : instructor.getStudents()) {
            student.getInstructors().remove(instructor);
        }
        instructor.getStudents().clear();

        // 5. Supprimer l’instructeur
        instructorsRepository.delete(instructor);
    }


    public List<InstructorsResponce> getInstructorsByName(String firstName) {
        var IntrucorsExsistng = instructorsRepository.findByFirstName(firstName);
        if (IntrucorsExsistng.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Instructor not found with name "+ firstName);
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




    public Instructors getInstructorsByEmail(String email) {
        return instructorsRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Instructor not found with email " + email)
                );
    }

    @Transactional
    public void deleteInstructorProfileById(int id) {
        // 1. Vérifier que l'instructeur existe
        Instructors instructor = instructorsRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Instructeur introuvable pour l'id " + id));

        // 2. Supprimer d'abord toutes les "progressions" (ou dépendances) liées à cet instructeur
        //    Ici, on suppose que Progression possède un champ instructor (ManyToOne)

        // 3. Puis supprimer l'instructeur lui‑même
        instructorsRepository.delete(instructor);
    }



    public Long getSubscriberCountForInstructor(Integer instructorId) {
        return instructorsRepository.countSubscribersByInstructorId(instructorId);
    }


}

