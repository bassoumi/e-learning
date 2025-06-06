package com.CRUD.firstApp.feedback;

import jakarta.persistence.criteria.CriteriaBuilder;

public record CommentRequest(
        Long contentId, Integer userId, String texte
) {
}
