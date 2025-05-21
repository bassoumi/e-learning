package com.CRUD.firstApp.courses;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CourseMapper {

    public Courses toEntityCourses(CoursRequest request){
        Courses courses = new Courses();
        courses.setTitle(request.title());
        courses.setDescription(request.description());
        courses.setCoverImage(request.coverImage());
        courses.setLanguage(request.language());
        courses.setLevel(request.level());
        courses.setShortDescription(request.shortDescription());
        if (request.metadata() != null) {
            CourseMetaDataRequest metaReq = request.metadata();

            CourseMetaData meta = new CourseMetaData();
            // set duration, tags, objectives from request
            meta.setDuration(metaReq.duration());
            meta.setTags(metaReq.tags());
            meta.setObjectives(metaReq.objectives());

            // initialize timestamps
            LocalDateTime now = LocalDateTime.now();
            meta.setCreatedAt(now);
            meta.setUpdatedAt(now);

            courses.setMetadata(meta);
        }        return courses ;

    }

    public CourseResponce toResponceCourses(Courses responce){

      return new CourseResponce(
              responce.getTitle(),
              responce.getDescription(),
              responce.getShortDescription(),
              responce.getLevel(),
              responce.getLanguage(),
              responce.getCoverImage()

      );

    }


}
