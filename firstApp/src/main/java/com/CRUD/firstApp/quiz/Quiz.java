package com.CRUD.firstApp.quiz;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;
import org.aspectj.weaver.patterns.TypePatternQuestions;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Quiz {
    @Id @GeneratedValue
    private int id;

    private String title;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestions> questions = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

}