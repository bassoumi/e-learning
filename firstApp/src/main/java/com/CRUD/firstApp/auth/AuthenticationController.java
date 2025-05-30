package com.CRUD.firstApp.auth;

import com.CRUD.firstApp.admin.AdminRequest;
import com.CRUD.firstApp.instructors.InstructorsRequest;
import com.CRUD.firstApp.student.StudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(
            path = "/register-instructor",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AuthentificationResponse> registerInstructor(
            @ModelAttribute @Valid InstructorsRequest request
    ) throws IOException {
        AuthentificationResponse response =
                authenticationService.registerInstructor(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthentificationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register-student")
    public ResponseEntity<AuthentificationResponse> registerStudent(@RequestBody StudentRequest request) {
        return ResponseEntity.ok(authenticationService.registerStudent(request));
    }


    @PostMapping("/register-admin")
    public ResponseEntity<AuthentificationResponse> registerAdmin(@RequestBody AdminRequest request) {
        return ResponseEntity.ok(authenticationService.registerAdmin(request));
    }


}
