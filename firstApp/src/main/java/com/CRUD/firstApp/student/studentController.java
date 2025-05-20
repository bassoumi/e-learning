package com.CRUD.firstApp.student;


import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/student")
public class studentController {

private final StudentService studentService;


    public studentController(StudentService studentService) {
        this.studentService = studentService;

    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }


    @PostMapping
    public StudentResponse addStudent(@Valid @RequestBody StudentRequest request) {
        return studentService.createStudent(request);
    }




}
