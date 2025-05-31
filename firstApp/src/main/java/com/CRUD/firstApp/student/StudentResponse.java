package com.CRUD.firstApp.student;

import java.util.List;

public record StudentResponse(
        int id,
        String firstName,
        String lastName,
        String gender,
        String email,
        List<Integer> instructorIds
){

}
