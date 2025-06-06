package com.CRUD.firstApp.feedback;

import java.time.LocalDateTime;

public record LikeRequest(
        Long id, Long userId, Long contentId, LocalDateTime dateLike
) {
}
