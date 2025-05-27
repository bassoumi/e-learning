package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.quiz.Quiz;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "enrollments",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categorie categorie;


    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "instructor_courses",
            joinColumns = @JoinColumn(name = "course_id"),         // correspond à la colonne "course_id" dans la table
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private List<Instructors> instructors = new ArrayList<>();


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Content> contents;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Quiz quiz;





}
