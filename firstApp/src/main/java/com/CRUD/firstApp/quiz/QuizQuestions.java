package com.CRUD.firstApp.quiz;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "quiz_question")
public class QuizQuestions {
    @Id
    @GeneratedValue
    private Long id;

    // ← back-pointer to Quiz: you must have this field...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;


    @Column(nullable = false)
    private String text;

    @ElementCollection
    @CollectionTable(
            name = "quiz_question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false)
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private String answer;
}
