package com.CRUD.firstApp.quiz;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
@SecurityRequirement(name ="bearerAuth")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }


    @GetMapping
    public List<QuizResponse> getAllQuiz() {
        return quizService.getAllQuiz();
    }

    @GetMapping("/{id}")
    public QuizResponse getQuizById(@PathVariable int id) {
        return quizService.getQuizById(id);
    }

    @GetMapping("title")
    public List<QuizResponse> findByTitle(@RequestParam String title) {
        return quizService.findByTitle(title);
    }

    @PostMapping
    public QuizResponse createQuiz(@RequestBody QuizRequest quizRequest) {
        return quizService.createQuiz(quizRequest);
    }

    @PutMapping("/{id}")
    public QuizResponse updateQuiz(@PathVariable int id ,@RequestBody QuizRequest quizRequest) {
        return quizService.updateQuiz(id , quizRequest );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable int id) {
         quizService.deleteQuiz(id);
         return ResponseEntity.ok("Quiz deleted with id " + id);
    }



}
