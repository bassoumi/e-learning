package com.CRUD.firstApp.instructors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InstructorsRequest(

        @NotBlank(message = "firstName cannot be empty" )
         String firstName,

        @NotBlank(message = "lastName cannot be empty" )
        String lastName,

        @Email(message = "email cannot be empty" )
        String email,

        @NotBlank(message = "profileImage cannot be empty" )
        String profileImage,

        @NotBlank(message = "bio cannot be empty" )
        String bio,

        @NotBlank(message = "password cannot be empty" )
        String password
) {
}
