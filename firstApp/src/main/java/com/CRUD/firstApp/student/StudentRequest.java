package com.CRUD.firstApp.student;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

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
        String password
){
}
