package com.CRUD.firstApp.courses;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Courses {
    @Id
    @GeneratedValue
    private int id;

    private String title;
    private String description;
    private String shortDescription;
    private String level;
    private String language;
    private String coverImage;

    @Embedded
    private CourseMetaData metadata;



}
