package com.CRUD.firstApp.quiz;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    public Quiz toEnity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());
        quiz.setQuestions(request.questions());
        quiz.setAnswers(request.answers());
        if (request.options() != null && !request.options().isEmpty()) {
            List<String> flatOptions = request.options().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            quiz.setOptions(flatOptions);
        }        return quiz;
    }

    public QuizResponse toResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getTitle(),
                quiz.getQuestions(),
                quiz.getAnswers(),
                quiz.getOptions()
        );
    }
}
