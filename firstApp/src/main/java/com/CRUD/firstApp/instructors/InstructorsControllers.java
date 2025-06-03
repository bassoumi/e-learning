package com.CRUD.firstApp.instructors;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorsControllers {

    private final InstructorsService instructorsService;

    public InstructorsControllers(InstructorsService instructorsService) {
        this.instructorsService = instructorsService;
    }

    @GetMapping
    public List<InstructorsResponce> getAllInstructors() {
        return instructorsService.getAllInstructors();
    }

    @GetMapping("/{id}")
    public Instructors getInstructorById(@PathVariable int id) {
        return instructorsService.getInstructorById(id);
    }


    @GetMapping("/{id}/profile")
    public InstructorsResponce getInstructorProfileById(@PathVariable int id) {
        return instructorsService.getInstructorProfileById(id);
    }

    @GetMapping("name")
    public List<InstructorsResponce> getInstructorsByName(@RequestParam String name) {
        return instructorsService.getInstructorsByName(name);
    }

    @GetMapping("email")
    public Instructors getInstructorsByEmail(@RequestParam String email) {
        return instructorsService.getInstructorsByEmail(email);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InstructorsResponce addInstructor(@ModelAttribute InstructorsRequest request) throws IOException {
        return instructorsService.addInstructor(request);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InstructorsResponce updateInstructor(
            @PathVariable int id,
            @ModelAttribute InstructorsRequest request
    ) throws IOException {
        return instructorsService.updateInstructor(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable int id) {
         instructorsService.deleteInstructor(id);
         return new ResponseEntity<>("Instructor Deleted with id " + id, HttpStatus.OK);
    }


}
