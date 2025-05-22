package com.CRUD.firstApp.instructors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public InstructorsResponce getInstructorById(@PathVariable int id) {
        return instructorsService.getInstructorById(id);
    }

    @GetMapping("name")
    public List<InstructorsResponce> getInstructorsByName(@RequestParam String name) {
        return instructorsService.getInstructorsByName(name);
    }

    @PostMapping
    public InstructorsResponce addInstructor(@RequestBody InstructorsRequest request) {
        return instructorsService.addInstructor(request);
    }
    @PutMapping("/{id}")
    public InstructorsResponce updateInstructor(@PathVariable int id , @RequestBody InstructorsRequest request) {
        return instructorsService.updateInstructor(id,request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable int id) {
         instructorsService.deleteInstructor(id);
         return new ResponseEntity<>("Instructor Deleted with id " + id, HttpStatus.OK);
    }


}
