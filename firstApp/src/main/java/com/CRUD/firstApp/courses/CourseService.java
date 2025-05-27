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

    public CourseService(CoursesRepository coursesRepository, CourseMapper courseMapper, CategorieService categorieService, InstructorsService instructorsService) {
        this.coursesRepository = coursesRepository;
        CourseMapper = courseMapper;
        this.categorieService = categorieService;
        this.instructorsService = instructorsService;
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
        // 1) Récupérer la catégorie
        Categorie category;
        try {
            category = categorieService.getEntityById(request.categoryId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : catégorie introuvable pour l'id " + request.categoryId()
            );
        }

        // 2) Récupérer l'instructeur
        Instructors instructor;
        try {
            instructor = instructorsService.getInstructorById(request.instructorId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : instructeur introuvable pour l'id " + request.instructorId()
            );
        }

        // 3) On crée l'entité Course (sans encore toucher à l'instructeur)
        Courses courseEntity = CourseMapper.toEntityCourses(request, category, new ArrayList<>());

        // 4) Définir la catégorie
        courseEntity.setCategorie(category);

        // 5) Ajouter l’instructeur DANS Courses.instructors (côté owning de la relation)
        courseEntity.getInstructors().add(instructor);

        // 6) (Optionnel) Pour garder la cohérence en mémoire, on peut aussi faire :
        instructor.getCourses().add(courseEntity);

        // 7) Sauvegarder : Hibernate va persister
        //    - la ligne dans courses (grâce à CourseMapper.toEntityCourses)
        //    - la ligne dans instructor_courses grâce au step 5
        Courses saved = coursesRepository.save(courseEntity);

        // 8) Retourner le DTO
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

    public CourseResponse updateCourses(@Valid int id, @Valid CoursRequest request) {
        var updatecourse = coursesRepository.findById(id);
        if (updatecourse.isEmpty()) {
            throw new RuntimeException("EURREUR UPDATE : Course not found with id " + id);
        }

      Courses exisitingValue = updatecourse.get();

       if (StringUtils.hasText(request.title())) {
           exisitingValue.setTitle(request.title());
       }
       if (StringUtils.hasText(request.description())) {
           exisitingValue.setDescription(request.description());
       }

       if (StringUtils.hasText(request.shortDescription())) {
           exisitingValue.setShortDescription(request.shortDescription());
       }

       if (StringUtils.hasText(request.level())) {
           exisitingValue.setLevel(request.level());
       }

       if (StringUtils.hasText(request.language())) {
           exisitingValue.setLanguage(request.language());
       }

       if (StringUtils.hasText(request.coverImage())) {
           exisitingValue.setCoverImage(request.coverImage());
       }

        if (request.metadata() != null) {
            CourseMetaDataRequest incoming = request.metadata();
            CourseMetaData stored       = exisitingValue.getMetadata();

            if (incoming.duration()    != null)                      stored.setDuration(incoming.duration());
            if (incoming.tags()        != null && !incoming.tags().isEmpty())        stored.setTags(incoming.tags());
            if (incoming.objectives()  != null && !incoming.objectives().isEmpty())  stored.setObjectives(incoming.objectives());

            // keep createdAt, bump updatedAt
            stored.setUpdatedAt(LocalDateTime.now());
            exisitingValue.setMetadata(stored);
        }

        // 4) Persist and map to response
        Courses saved = coursesRepository.save(exisitingValue);
        var courseResponce = CourseMapper.toResponceCourses(saved);
        return courseResponce;


    }
}
