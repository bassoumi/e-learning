package com.CRUD.firstApp.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByUserIdAndContenuCoursId(Long userId, Long contenuCoursId);
    long countByContenuCoursId(Long contentId);

}
