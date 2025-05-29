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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ← back-pointer vers Quiz : ce champ doit impérativement être renseigné (nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
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
