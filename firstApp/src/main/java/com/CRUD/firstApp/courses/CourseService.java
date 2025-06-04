package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.Categorie.CategorieService;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentMapper;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import com.CRUD.firstApp.instructors.InstructorsService;
import com.CRUD.firstApp.notification.NotificationService;
import com.CRUD.firstApp.quiz.Quiz;
import com.CRUD.firstApp.quiz.QuizMapper;
import com.CRUD.firstApp.quiz.QuizRequest;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CoursesRepository coursesRepository;
    private final CourseMapper CourseMapper;
    private final CategorieService categorieService;
    private final InstructorsService instructorsService;
    private final CourseFileStorageService courseFileStorageService;
    private final NotificationService notificationService;
    private final EntityManager entityManager;


    public CourseService(CoursesRepository coursesRepository, CourseMapper courseMapper, CategorieService categorieService, InstructorsService instructorsService, CourseFileStorageService courseFileStorageService, NotificationService notificationService, EntityManager entityManager) {
        this.coursesRepository = coursesRepository;
        CourseMapper = courseMapper;
        this.categorieService = categorieService;
        this.instructorsService = instructorsService;
        this.courseFileStorageService = courseFileStorageService;
        this.notificationService = notificationService;
        this.entityManager = entityManager;
    }

    public List<CourseResponse> getCourses() {
        List<Courses> courses = coursesRepository.findAll();
        // Pour chaque course, forcer l'initialisation de la collection:
        courses.forEach(c -> Hibernate.initialize(c.getInstructor()));
        return courses.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());

    }
    @Transactional
    public CourseResponse addCourse(CoursRequest request) {
        // Récupération directe (exception lancée si pas trouvé)
        Categorie category = categorieService.getEntityById(request.categoryId()); // ✅ this is an Integer or int
        Instructors instructor = instructorsService.getInstructorById(request.instructorId());

        // Sauvegarde de l’image
        String storedFilename = courseFileStorageService.storeFile(request.coverImage());

        // Mapping en entité
        Courses courseEntity = CourseMapper.toEntityCourses(
                request,
                category,
                instructor,   // on passe directement la liste contenant l’instructeur
                storedFilename
        );

        // Persistance (inclut la M-to-M)
        Courses saved = coursesRepository.save(courseEntity);
        notificationService.notifyNewCourseToSubscribers(saved);

        // Retour du DTO
        return CourseMapper.toResponceCourses(saved);
    }

    public List<CourseResponse> getCourseById(int id) {
        var coursesEntityById = coursesRepository.findById(id);
        if (coursesEntityById.isEmpty()) {
            throw new RuntimeException("Course not found with id " + id);
        }
        return coursesEntityById.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByTitle(String title) {
        List<Courses> found = coursesRepository
                .findByTitleContainingIgnoreCase(title.trim());
        // simply return empty list if none—no exception
        return found.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteCourseById(int id) {
        // 1) Charger le Course (ou renvoyer 404 s’il n’existe pas)
        Courses course = coursesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + id));

        // 2) Purger d’abord toutes les lignes dans la table de jointure "instructor_courses".
        //    → On utilise une requête SQL native, car on n’a pas d’entité JPA pour cette table.
        //
        //    ATTENTION : adaptez le nom exact de la colonne de jointure si ce n’est pas "cours_id".
        //    D’après votre base, le join‐column dans instructor_courses doit s’appeler "cours_id".
        entityManager.createNativeQuery(
                        "DELETE FROM instructor_courses WHERE course_id = :courseId"
                )
                .setParameter("courseId", id)
                .executeUpdate();

        // 3) Détacher la relation Many-To-One vers l’instructeur (côté objet),
        //    afin que la colonne instructor_id passe à NULL si besoin (reste cohérent).
        Instructors instr = course.getInstructor();
        if (instr != null) {
            instr.getCourses().remove(course);
            course.setInstructor(null);
        }

        // 4) Détacher la relation Many-To-Many vers les étudiants
        for (Student stu : course.getStudents()) {
            stu.getCourses().remove(course);
        }
        course.getStudents().clear();

        // 5) Supprimer enfin le Course
        coursesRepository.delete(course);
        //    —> Hibernate flushera les entités et ne remontera plus d’erreur FK,
        //        car la jointure "instructor_courses" a déjà été vidée.
    }

    @Transactional
    public CourseResponse updateCourses(int id, CoursRequest request) {
        // 1. Vérification de l’existence du course
        Courses existingValue = coursesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERREUR UPDATE : Course not found with id " + id));

        // 2. Mise à jour des champs simples
        if (StringUtils.hasText(request.title())) {
            existingValue.setTitle(request.title());
        }
        if (StringUtils.hasText(request.description())) {
            existingValue.setDescription(request.description());
        }
        if (StringUtils.hasText(request.shortDescription())) {
            existingValue.setShortDescription(request.shortDescription());
        }
        if (StringUtils.hasText(request.level())) {
            existingValue.setLevel(request.level());
        }
        if (StringUtils.hasText(request.language())) {
            existingValue.setLanguage(request.language());
        }

        // 3. Mise à jour de l’image de couverture (coverImage) si fournie
        MultipartFile newImage = request.coverImage();
        if (newImage != null && !newImage.isEmpty()) {
            String storedFilename = courseFileStorageService.storeFile(newImage);
            existingValue.setCoverImage(storedFilename);
        }

        // 4. Mise à jour des métadonnées si fournies
        if (request.metadata() != null) {
            CourseMetaDataRequest incoming = request.metadata();
            CourseMetaData stored = existingValue.getMetadata();
            if (stored == null) {
                stored = new CourseMetaData();
            }
            if (incoming.duration() != null) {
                stored.setDuration(incoming.duration());
            }
            if (incoming.tags() != null && !incoming.tags().isEmpty()) {
                stored.setTags(incoming.tags());
            }
            if (incoming.objectives() != null && !incoming.objectives().isEmpty()) {
                stored.setObjectives(incoming.objectives());
            }
            stored.setUpdatedAt(LocalDateTime.now());
            existingValue.setMetadata(stored);
        }

        // 5. Sauvegarde finale
        Courses saved = coursesRepository.save(existingValue);
        return CourseMapper.toResponceCourses(saved);
    }



    public List<CourseResponse> getCoursesByCategory(int categoryId) {
        List<Courses> courses = coursesRepository.findByCategorie_Id(categoryId);
        return courses.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());
    }



    public List<CourseResponse> getCoursesByInstructor(int instructorId) {
        List<Courses> courses = coursesRepository.findByInstructor_Id(instructorId);
        return courses.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());
    }


}
