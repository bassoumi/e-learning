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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    // 1️⃣ Relation « Quiz → QuizQuestions »
    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuizQuestions> questions = new ArrayList<>();

    // 2️⃣ Relation « Quiz → Courses »
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Courses course;

    /**
     * Helper: add a question and automatically set its quiz back-pointer
     */
    public void addQuestion(QuizQuestions question) {
        questions.add(question);
        question.setQuiz(this);
    }

    /**
     * Helper: remove a question and clear its quiz reference
     */
    public void removeQuestion(QuizQuestions question) {
        questions.remove(question);
        question.setQuiz(null);
    }

    /**
     * (Optional) Helper to change course if you want symmetry on the other side
     */
    public void setCourse(Courses course) {
        this.course = course;
        if (course.getQuiz() != this) {
            course.setQuiz(this);
        }
    }
}
