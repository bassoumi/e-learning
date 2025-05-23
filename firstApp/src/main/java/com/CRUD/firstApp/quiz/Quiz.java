package com.CRUD.firstApp.quiz;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Quiz {
    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String questions;
    private String options;
    private String answers;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Courses course;
}
