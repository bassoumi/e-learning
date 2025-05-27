package com.CRUD.firstApp.quiz;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Quiz {
    @Id @GeneratedValue
    private int id;

    private String title;

    // questions as a collection of Strings
    @ElementCollection
    @CollectionTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id")
    )
    @Column(name = "question")
    private List<String> questions;

    // options as a collection of STRINGS (flattened).
    // If you really want List<List<String>>, you need a join table per sub‐list.
    @ElementCollection
    @CollectionTable(
            name = "quiz_options",
            joinColumns = @JoinColumn(name = "quiz_id")
    )
    @Column(name = "option_text")
    private List<String> options;

    // answers as a collection of Strings
    @ElementCollection
    @CollectionTable(
            name = "quiz_answers",
            joinColumns = @JoinColumn(name = "quiz_id")
    )
    @Column(name = "answer")
    private List<String> answers;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Courses course;

    // … constructors/getters/setters …
}