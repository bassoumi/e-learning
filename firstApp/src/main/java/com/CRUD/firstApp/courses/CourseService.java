package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.Categorie.CategorieService;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
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
        return coursesRepository.findAll()
                .stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());

    }

    public CourseResponse addCourse(CoursRequest request) {
        Categorie category;
        try {
            category = categorieService.getEntityById(request.categoryId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : catégorie introuvable pour l'id " + request.categoryId()
            );
        }

        // 2) Même mécanique pour l’Instructor
        Instructors instructor;
        try {
            instructor = instructorsService.getInstructorById(request.instructorId());
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(
                    "Impossible de créer le cours : instructeur introuvable pour l'id " + request.instructorId()
            );
        }

        // 3) Mapping + persistence
        Courses courseEntity = CourseMapper.toEntityCourses(
                request,
                category,
                List.of(instructor)
        );
        courseEntity.setCategorie(category);
        courseEntity.setInstructors(List.of(instructor));
        Courses saved = coursesRepository.save(courseEntity);



        // 4) Retour du DTO
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
