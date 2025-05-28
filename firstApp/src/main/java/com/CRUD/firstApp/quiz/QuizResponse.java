package com.CRUD.firstApp.quiz;

import java.util.List;

public record QuizResponse(
        int id,
        String title,
        Integer courseId,
        List<QuestionResponse> questions      // add courseId here
) {}

