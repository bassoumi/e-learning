package com.CRUD.firstApp.quiz;

import java.util.List;

public record QuestionResponse (
        String text,
        List<String> options,
        String answer
) {
}
