package com.CRUD.firstApp.auth;

import com.CRUD.firstApp.admin.AdminRequest;
import com.CRUD.firstApp.instructors.InstructorsRequest;
import com.CRUD.firstApp.student.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register-Instructor")
    public ResponseEntity<AuthentificationResponse> registerInstructor(@RequestBody InstructorsRequest request) {
        return ResponseEntity.ok(authenticationService.registerInstructor(request));

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
