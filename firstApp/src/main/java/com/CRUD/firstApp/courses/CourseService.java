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
        // 1) Charger le Course (ou renvoyer 404)
        Courses course = coursesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + id));

        // 2) Supprimer la jointure instructor_courses
        entityManager.createNativeQuery(
                        "DELETE FROM instructor_courses WHERE course_id = :courseId"
                )
                .setParameter("courseId", id)
                .executeUpdate();

        // 3) Détacher l’instructeur (Many-To-One)
        Instructors instr = course.getInstructor();
        if (instr != null) {
            instr.getCourses().remove(course);
            course.setInstructor(null);
        }

        // 4) Détacher les étudiants (Many-To-Many)
        for (Student stu : course.getStudents()) {
            stu.getCourses().remove(course);
        }
        course.getStudents().clear();

        // ─────────────────────────────────────────────────────────────────
        // 5a) Récupérer la liste des IDs de tous les Content liés à ce Course
        @SuppressWarnings("unchecked")
        List<Integer> contentIds = entityManager.createQuery(
                        "SELECT c.id FROM Content c WHERE c.course.id = :courseId"
                )
                .setParameter("courseId", id)
                .getResultList();

        if (!contentIds.isEmpty()) {
            // 5b) SUPPRIMER toutes les Progression liées à ces contenus
            entityManager.createQuery(
                            "DELETE FROM Progression p WHERE p.contentEnCours.id IN :ids"
                    )
                    .setParameter("ids", contentIds)
                    .executeUpdate();

            // 5c) SUPPRIMER les Content eux-mêmes
            entityManager.createNativeQuery(
                            "DELETE FROM content WHERE course_id = :courseId"
                    )
                    .setParameter("courseId", id)
                    .executeUpdate();
        }

        // ─────────────────────────────────────────────────────────────────
        // 6) SUPPRIMER toutes les Notifications liées à ce Course
        entityManager.createQuery(
                        "DELETE FROM Notification n WHERE n.course.id = :courseId"
                )
                .setParameter("courseId", id)
                .executeUpdate();

        // ─────────────────────────────────────────────────────────────────
        // 7) SUPPRIMER toutes les entrées d’Agenda liées à ce Course
        entityManager.createQuery(
                        "DELETE FROM Agenda a WHERE a.course.id = :courseId"
                )
                .setParameter("courseId", id)
                .executeUpdate();

        // ─────────────────────────────────────────────────────────────────
        // 8) Enfin, supprimer le Course lui-même
        coursesRepository.delete(course);
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
