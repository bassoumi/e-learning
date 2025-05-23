package com.CRUD.firstApp.quiz;


import org.springframework.stereotype.Component;

@Component
public class QuizMapper {

    public Quiz toEnity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());
        quiz.setQuestions(request.questions());
        quiz.setAnswers(request.answers());
        quiz.setOptions(request.options());
        return quiz;
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
