package com.CRUD.firstApp.quiz;

import java.util.List;

public record QuestionRequest (
        String text,
        List<String> options,
        String answer
){

}
