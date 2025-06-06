package com.CRUD.firstApp.feedback;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // COMMENTS
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        CommentResponse response = feedbackService.addComment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/{contentId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long contentId) {
        List<CommentResponse> comments = feedbackService.getCommentsByContent(contentId);
        return ResponseEntity.ok(comments);
    }

    // LIKES
    @PostMapping("/likes")
    public ResponseEntity<LikeResponse> addLike(@RequestBody LikeRequest request) {
        LikeResponse response = feedbackService.addLike(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/likes")
    public ResponseEntity<Void> removeLike(@RequestBody LikeRequest request) {
        feedbackService.removeLike(request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/likes/count/{contentId}")
    public ResponseEntity<Long> countLikes(@PathVariable Long contentId) {
        long count = feedbackService.countLikesByContent(contentId);
        return ResponseEntity.ok(count);
    }

    // Compter les commentaires d'un contenu
    @GetMapping("/comments/count/{contentId}")
    public ResponseEntity<Long> countComments(@PathVariable Long contentId) {
        long count = feedbackService.countCommentsByContent(contentId);
        return ResponseEntity.ok(count);
    }



}
