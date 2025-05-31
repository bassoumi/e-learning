package com.CRUD.firstApp.student;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record StudentRequest (


        @NotBlank(message = "First name must not be empty")
         String firstName,

        @NotBlank(message = "Last name must not be empty")
        String lastName,
         @Positive(message = "age must be positive ")
         int age,
         String gender,
         @Email(message = "email should be valid ")
         String email,
         String phone,
         Address address,

        @NotBlank(message = "password must not be empty")
        String password,

        @NotEmpty(message = "Instructor IDs must not be empty")
        List<@Positive(message = "Instructor ID must be positive") Integer> instructorIds
){
}
