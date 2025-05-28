package com.CRUD.firstApp.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuizRequest (
        String title,
        Integer courseId,
        List<QuestionRequest> questions
){
}
