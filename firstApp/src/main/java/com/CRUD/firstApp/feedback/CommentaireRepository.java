package com.CRUD.firstApp.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByContenuCoursId(Long contentId);
    long countByContenuCoursId(Long contentId);

}
