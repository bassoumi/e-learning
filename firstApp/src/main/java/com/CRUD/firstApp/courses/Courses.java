// Courses.java
package com.CRUD.firstApp.courses;

import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.quiz.Quiz;
import com.CRUD.firstApp.student.Student;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
// <-- On demande à Jackson de sérialiser cette entité par son "id" une fois déjà vue.

public class Courses {
    @Id
    @GeneratedValue
    private int id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 500)
    private String shortDescription;
    private String level;
    private String language;
    @Column(length = 300)
    private String coverImage;

    @Embedded
    private CourseMetaData metadata;

    @ManyToMany
    @JoinTable(
            name = "enrollments",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    // Student est déjà annoté avec @JsonIdentityInfo, donc ici on ne met pas @JsonIgnore.
    private Set<Student> students = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "instructor_id")
    private Instructors instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @OneToOne(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Quiz quiz;
}
