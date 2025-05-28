package com.CRUD.firstApp.quiz;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    public Quiz toEntity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());

        if (request.questions() != null) {
            List<QuizQuestions> qEntities = request.questions().stream()
                    .map(qr -> {
                        QuizQuestions q = new QuizQuestions();
                        q.setQuiz(quiz);   // back-reference set here!
                        q.setText(qr.text());
                        q.setOptions(new ArrayList<>(qr.options()));
                        q.setAnswer(qr.answer());
                        return q;
                    })
                    .toList();
            quiz.setQuestions(qEntities);
        }
        return quiz;
    }


    public QuizResponse toResponse(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        List<QuestionResponse> qDtos = quiz.getQuestions() != null ?
                quiz.getQuestions().stream()
                        .map(q -> new QuestionResponse(
                                q.getText(),
                                q.getOptions(),
                                q.getAnswer()
                        ))
                        .toList() : Collections.emptyList();

        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getCourse() != null ? quiz.getCourse().getId() : null,
                qDtos
        );
    }
}