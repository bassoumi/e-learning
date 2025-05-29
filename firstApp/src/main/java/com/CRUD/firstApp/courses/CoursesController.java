package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.contentcourse.ContentRequest;
import com.CRUD.firstApp.quiz.QuizRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.hibernate.boot.Metadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
public class CoursesController {

    private final CourseService CourseService;
    private final ObjectMapper objectMapper;

    public CoursesController(CourseService CourseService, ObjectMapper objectMapper) {
        this.CourseService = CourseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<CourseResponse> getCourses() {
        return CourseService.getCourses();
    }


    @GetMapping("/category/{categoryId}")
    public List<CourseResponse> getCoursesByCategory(@PathVariable int categoryId) {
        return CourseService.getCoursesByCategory(categoryId);
    }
        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CourseResponse> addCourse(
                @RequestParam("title") String title,
                @RequestParam("description") String description,
                @RequestParam(value = "shortDescription", required = false) String shortDescription,
                @RequestParam("categoryId") Integer categoryId,
                @RequestParam("level") String level,
                @RequestParam("language") String language,
                @RequestParam("instructorId") Integer instructorId,
                @RequestPart("contents") MultipartFile contentsFile,
                @RequestPart("quiz") MultipartFile quizFile,
                @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                @RequestPart(value = "metadata", required = false) MultipartFile metadataFile

        ) throws IOException {
            // 1. Désérialisation JSON
            String contentsJson = new String(contentsFile.getBytes(), StandardCharsets.UTF_8);
            String quizJson     = new String(quizFile.getBytes(), StandardCharsets.UTF_8);

            List<ContentRequest> contents = objectMapper.readValue(
                    contentsJson, new TypeReference<List<ContentRequest>>() {}
            );
            QuizRequest quiz = objectMapper.readValue(quizJson, QuizRequest.class);

            CourseMetaDataRequest metaReq = null;
            if (metadataFile != null) {
                String metaJson = new String(metadataFile.getBytes(), StandardCharsets.UTF_8);
                metaReq = objectMapper.readValue(metaJson, CourseMetaDataRequest.class);
            }

            // 2. Construction du DTO (ordre EXACT du record !)
            CoursRequest coursRequest = new CoursRequest(
                    title,
                    description,
                    (shortDescription == null) ? "" : shortDescription,
                    categoryId,
                    (level == null) ? "" : level,
                    (language == null) ? "" : language,
                    instructorId,
                    contents,      // 8ᵉ param : List<ContentRequest>
                    quiz,          // 9ᵉ param : QuizRequest
                    coverImage,
                    metaReq // 10ᵉ param : MultipartFile
            );

            // 3. Appel au service métier
            CourseResponse savedResponse = CourseService.addCourse(coursRequest);

            // 4. Retour 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
        }






    @GetMapping("/{id}")
    public List<CourseResponse> getCourseById(@PathVariable int id) {
        return CourseService.getCourseById(id);
    }

    @GetMapping("/title")
    public List<CourseResponse> getCourseBytitle(@RequestParam String title) {
        return CourseService.getCoursesByTitle(title);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteCourseById( @Valid @PathVariable int id) {
         CourseService.deleteCourseById(id);
         return ResponseEntity.ok("course with deleted with id "+id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CourseResponse updateCourse(@PathVariable int id, @ModelAttribute CoursRequest request) {
        return CourseService.updateCourses(id, request);
    }


}
