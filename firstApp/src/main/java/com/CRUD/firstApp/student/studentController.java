package com.CRUD.firstApp.student;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.notification.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

// subscribe method


    @GetMapping("/{studentId}/subscribe")
    public StudentResponse getInstructorForStudent(@PathVariable int studentId) {
        System.out.println(">>> Début de GET /students/" + studentId + "/subscribe");
        StudentResponse response = studentService.getSubscription(studentId);
        System.out.println("<<< Fin de GET, réponse envoyée : " + response);
        return response;
    }

    @PostMapping("/{studentId}/subscribe/{instructorId}")
    public StudentResponse subscribeToInstructor(
            @PathVariable int studentId,
            @PathVariable int instructorId) {

        return studentService.subscribeToInstructor(studentId, instructorId);
    }


    @PutMapping("/{studentId}/subscribe")
    public StudentResponse updateSubscription(
            @PathVariable int studentId,
            @RequestBody @Valid SubscriptionRequest request) {

        List<Integer> newInstructorIds = request.getInstructorIds();
        return studentService.updateSubscription(studentId, newInstructorIds);

    }

    @DeleteMapping("/{studentId}/subscribe/{instructorId}")
    public ResponseEntity<Void> unsubscribeFromInstructor(
            @PathVariable int studentId,
            @PathVariable int instructorId) {

        studentService.unsubscribeFromInstructor(studentId, instructorId);
        // Répond un 204 No Content (body vide) pour indiquer que tout s’est bien passé
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/notifications")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@PathVariable int studentId) {
        List<Notification> entities = studentService.getUnreadNotifications(studentId);

        List<NotificationDto> dtos = entities.stream().map(n -> {
            Courses course = n.getCourse();
            Instructors instructor = course.getInstructor();
            Categorie category = course.getCategorie(); // ou getCategoryName() selon ton modèle

            return new NotificationDto(
                    n.getId(),
                    course.getId(),
                    course.getTitle(),
                    course.getShortDescription(),
                    course.getLevel(),
                    course.getLanguage(),
                    instructor.getFirstName()+""+instructor.getLastName(), // ou getFirstName() + " " + getLastName()
                    instructor.getId(),
                    category.getNom(),
                    category.getId(),
                    course.getCoverImage(),
                    n.getCreatedAt()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }



    @PutMapping("/{studentId}/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable int studentId,
            @PathVariable Integer notificationId) {

        studentService.markNotificationAsRead(studentId, notificationId);
        return ResponseEntity.noContent().build();
    }


}
