package com.CRUD.firstApp.feedback;

import java.time.LocalDateTime;

public record CommentResponse(Long id, String texte,  String studentName , LocalDateTime dateCreation) {
}
