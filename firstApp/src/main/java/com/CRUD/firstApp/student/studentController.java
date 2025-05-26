package com.CRUD.firstApp.student;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/student")
@Tag(name = "Student")
public class studentController {
    private final StudentService studentService;
    public studentController(StudentService studentService) {
        this.studentService = studentService;

    }


    @Operation(
            description = "Get endpoint for manager",
            summary = "this is a summary for student get endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),

            }
    )
    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents();
    }



    @PostMapping
    public StudentResponse addStudent(@Valid @RequestBody StudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById( @PathVariable int id ) {
        return  studentService.getStudentById(id);
    }

    @GetMapping("/search")
    public List<StudentResponse> getStudentsByName(@RequestParam(required = true) @NotBlank String name) {
        return studentService.getStudentsByName(name);
    }

    @GetMapping("/email")
    public StudentResponse getStudentsByEmail(@RequestParam(required = true) @NotBlank String email) {
        return studentService.getStudentsByEmail(email);
    }

    @PutMapping("/{id}")
    public StudentResponse patchStudent(
            @PathVariable int id,
            @RequestBody StudentRequest request
    ) {
        return studentService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent( @PathVariable int id ) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully for ID: " + id);

    }




}
