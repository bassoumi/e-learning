package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.Categorie.CategorieService;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsService;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CoursesRepository coursesRepository;
    private final CourseMapper CourseMapper;
    private final CategorieService categorieService;
    private final InstructorsService instructorsService;
    private final CourseFileStorageService courseFileStorageService;

    public CourseService(CoursesRepository coursesRepository, CourseMapper courseMapper, CategorieService categorieService, InstructorsService instructorsService, CourseFileStorageService courseFileStorageService) {
        this.coursesRepository = coursesRepository;
        CourseMapper = courseMapper;
        this.categorieService = categorieService;
        this.instructorsService = instructorsService;
        this.courseFileStorageService = courseFileStorageService;
    }

    public List<CourseResponse> getCourses() {
        List<Courses> courses = coursesRepository.findAll();
        // Pour chaque course, forcer l'initialisation de la collection:
        courses.forEach(c -> Hibernate.initialize(c.getInstructors()));
        return courses.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());

    }
    @Transactional
    public CourseResponse addCourse(CoursRequest request) {
        // 1) Récupérer la catégorie à partir de l’ID présent dans le record
        Categorie category;
        try {
            category = categorieService.getEntityById(request.categoryId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : catégorie introuvable pour l’ID " + request.categoryId()
            );
        }

        // 2) Récupérer l’instructeur à partir de l’ID présent dans le record
        Instructors instructor;
        try {
            instructor = instructorsService.getInstructorById(request.instructorId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : instructeur introuvable pour l’ID " + request.instructorId()
            );
        }

        // 3) Sauvegarder l’image et récupérer le nom du fichier
        String storedFilename = courseFileStorageService.storeFile(request.coverImage());

        // 4) Mapper vers l’entité Courses (en passant le titre, la catégorie, la liste d’instructeurs vide, et le nom de fichier)
        Courses courseEntity = CourseMapper.toEntityCourses(
                request,
                category,
                new ArrayList<>(),    // liste vide d’instructeurs pour l’instant
                storedFilename        // nom ou chemin du fichier d’image
        );

        // 5) Définir la catégorie
        courseEntity.setCategorie(category);

        // 6) Ajouter l’instructeur DANS courseEntity (côté owning)
        courseEntity.getInstructors().add(instructor);

        // 7) (Optionnel) Mettre à jour côté instructeur pour la cohérence en mémoire
        instructor.getCourses().add(courseEntity);

        // 8) Sauvegarder l’entité Courses (Hibernate gère la jointure ManyToMany)
        Courses saved = coursesRepository.save(courseEntity);

        // 9) Mapper l’entité sauvegardée vers le DTO de réponse
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

    public List<CourseResponse> getCourseBytitle(String title) {
        var CourseEntityBytitle = coursesRepository.findByTitle(title);
        if (CourseEntityBytitle.isEmpty()) {
            throw new RuntimeException("Course not found with name " + title);
        }
        return CourseEntityBytitle.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());    }

    public void deleteCourseById(int id) {
        coursesRepository.deleteById(id);

    }

    @Transactional
    public CourseResponse updateCourses(@Valid int id, @Valid CoursRequest request) {
        var updateCourse = coursesRepository.findById(id);
        if (updateCourse.isEmpty()) {
            throw new RuntimeException("ERREUR UPDATE : Course not found with id " + id);
        }

        Courses existingValue = updateCourse.get();

        // Mettre à jour les champs simples
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

        // Mettre à jour l’image si une nouvelle est envoyée
        MultipartFile newImage = request.coverImage();
        if (newImage != null && !newImage.isEmpty()) {
            String storedFilename = courseFileStorageService.storeFile(newImage);
            existingValue.setCoverImage(storedFilename);
        }

        // Mettre à jour la catégorie si elle a changé
        if (request.categoryId() != null && !request.categoryId().equals(existingValue.getCategorie().getId())) {
            Categorie newCategory = categorieService.getEntityById(request.categoryId());
            existingValue.setCategorie(newCategory);
        }

        // Mettre à jour l’instructeur si nécessaire (optionnel selon ton besoin métier)
        if (request.instructorId() != null) {
            Instructors newInstructor = instructorsService.getInstructorById(request.instructorId());
            existingValue.getInstructors().clear(); // remplace le ou les anciens instructeurs
            existingValue.getInstructors().add(newInstructor);
        }

        // Mettre à jour les métadonnées
        if (request.metadata() != null) {
            CourseMetaDataRequest incoming = request.metadata();
            CourseMetaData stored = existingValue.getMetadata();
            if (stored == null) {
                stored = new CourseMetaData();
            }

            if (incoming.duration() != null) stored.setDuration(incoming.duration());
            if (incoming.tags() != null && !incoming.tags().isEmpty()) stored.setTags(incoming.tags());
            if (incoming.objectives() != null && !incoming.objectives().isEmpty()) stored.setObjectives(incoming.objectives());

            stored.setUpdatedAt(LocalDateTime.now());
            existingValue.setMetadata(stored);
        }

        // Enregistrer et retourner
        Courses saved = coursesRepository.save(existingValue);
        return CourseMapper.toResponceCourses(saved);
    }

}
