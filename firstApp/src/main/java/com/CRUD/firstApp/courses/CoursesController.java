package com.CRUD.firstApp.courses;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CoursesController {

    private final CourseService CourseService;

    public CoursesController(CourseService CourseService) {
        this.CourseService = CourseService;
    }

    @GetMapping
    public List<CourseResponse> getCourses() {
        return CourseService.getCourses();
    }

    @PostMapping
    public CourseResponse addCourse(@Valid @RequestBody CoursRequest request) {
        return CourseService.addCourse(request);
    }

    @GetMapping("/{id}")
    public List<CourseResponse> getCourseById(@PathVariable int id) {
        return CourseService.getCourseById(id);
    }

    @GetMapping("/title")
    public List<CourseResponse> getCourseBytitle(@RequestParam String title) {
        return CourseService.getCourseBytitle(title);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteCourseById( @Valid @PathVariable int id) {
         CourseService.deleteCourseById(id);
         return ResponseEntity.ok("course with deleted with id "+id);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable int id , @RequestBody   CoursRequest request) {
       return CourseService.updateCourses(id,request);
    }

}
