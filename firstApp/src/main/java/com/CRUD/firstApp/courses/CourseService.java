package com.CRUD.firstApp.courses;


import ch.qos.logback.core.util.StringUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
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

    public CourseService(CoursesRepository coursesRepository, CourseMapper CourseMapper, CourseMapper courseMapper) {
        this.coursesRepository = coursesRepository;
        this.CourseMapper = CourseMapper;
    }

    public List<CourseResponce> getCourses() {
        return coursesRepository.findAll()
                .stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());

    }

    public CourseResponce addCourse(@Valid @RequestBody CoursRequest request) {
        var coursesEntity = CourseMapper.toEntityCourses(request);
        coursesRepository.save(coursesEntity);
        return CourseMapper.toResponceCourses(coursesEntity);
    }


    public List<CourseResponce> getCourseById(int id) {
        var coursesEntityById = coursesRepository.findById(id);
        if (coursesEntityById.isEmpty()) {
            throw new RuntimeException("Course not found with id " + id);
        }
        return coursesEntityById.stream()
                .map(CourseMapper::toResponceCourses)
                .collect(Collectors.toList());
    }

    public List<CourseResponce> getCourseBytitle(String title) {
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

    public CourseResponce updateCourses(@Valid int id, @Valid CoursRequest request) {
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
