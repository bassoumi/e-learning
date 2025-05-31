package com.CRUD.firstApp.progression;


import com.CRUD.firstApp.courses.CourseResponseProgress;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ProgressionContoller {

    private final ProgressionService progressionService;



    @GetMapping("/students/{studentId}/progressions")
    public ResponseEntity<List<ProgressionResponce>> listByStudent(
            @PathVariable Integer studentId
    ) {
        List<ProgressionResponce> list = progressionService.listByStudent(studentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/students/{studentId}/in-progress-courses")
    public ResponseEntity<List<CourseResponseProgress>> getInProgressCourses(
            @PathVariable Integer studentId
    ) {
        List<CourseResponseProgress> list = progressionService.listInProgressCoursesWithLastViewed(studentId);
        return ResponseEntity.ok(list);
    }


    @PostMapping("/students/{studentId}/contents/{contentId}/progression")
    public ResponseEntity<ProgressionResponce> addProgression(
            @PathVariable Integer studentId,
            @PathVariable Integer contentId,
            @RequestBody @Valid ProgressionRequest request
    ) {
        ProgressionResponce res = progressionService.addProgression(studentId, contentId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @GetMapping("/{id}")
    public ProgressionResponce getProgressionById(@PathVariable int id) {
        return progressionService.getProgressionById(id);

    }

    @PutMapping("/students/{studentId}/contents/{contentId}/progression")
    public ResponseEntity<ProgressionResponce> updateProgression(
            @PathVariable Integer studentId,
            @PathVariable Integer contentId,
            @RequestBody ProgressionRequest request
    ) {
        ProgressionResponce res = progressionService
                .updateProgression(studentId, contentId, request);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id) {
         progressionService.deleteProgression(id);
         return ResponseEntity.ok("Progression deleted");

    }

}
