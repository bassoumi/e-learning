package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.contentcourse.Content;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            meta.setDuration(metaReq.duration());
            meta.setTags(metaReq.tags());
            meta.setObjectives(metaReq.objectives());

            LocalDateTime now = LocalDateTime.now();
            meta.setCreatedAt(now);
            meta.setUpdatedAt(now);

            courses.setMetadata(meta);
        }

        // ✅ Mapper les contenus ici
        if (request.contents() != null && !request.contents().isEmpty()) {
            List<Content> contents = request.contents().stream()
                    .map(contentReq -> {
                        Content content = new Content();
                        content.setTitle(contentReq.title());
                        content.setDescription(contentReq.description());
                        content.setVideoUrl(contentReq.videoUrl());
                        content.setOrderContent(contentReq.orderContent());
                        content.setCourse(courses); // Lien vers le cours
                        return content;
                    })
                    .collect(Collectors.toList());

            courses.setContents(contents);
        }

        return courses;
    }


    public CourseResponce toResponceCourses(Courses responce){

      return  null ;




    }


}
