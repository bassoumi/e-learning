package com.CRUD.firstApp.instructors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record InstructorsRequest(

        @NotBlank(message = "firstName cannot be empty") String firstName,
        @NotBlank(message = "lastName cannot be empty")  String lastName,
        @Email   (message = "email must be valid")       String email,
        @NotNull (message = "profileImage cannot be empty") MultipartFile profileImage,
        @NotBlank(message = "bio cannot be empty")       String bio,
        @NotBlank(message = "password cannot be empty")  String password
) {
}
