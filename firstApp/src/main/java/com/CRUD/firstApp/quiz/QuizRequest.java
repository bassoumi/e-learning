package com.CRUD.firstApp.quiz;

public record QuizRequest (
        String title,
        String questions,
        String options,
        String answers
){
}
