package com.CRUD.firstApp.quiz;

import java.util.List;

public record QuizResponse(
         String title,
         List<String> questions,
         List<String> answers,
         List<String> options
         ) {
}
