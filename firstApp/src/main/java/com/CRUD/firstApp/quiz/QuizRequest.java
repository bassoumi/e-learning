package com.CRUD.firstApp.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuizRequest (
        @NotBlank String           title,
        @NotEmpty  List<String>    questions,
        @NotEmpty  List<List<String>> options,
        @NotEmpty List<String> answers
){
}
