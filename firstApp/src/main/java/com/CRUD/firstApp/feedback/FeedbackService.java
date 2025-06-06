package com.CRUD.firstApp.feedback;


import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    private final CommentaireRepository commentRepo;
    private final LikeRepository likeRepo;
    private final ContentRepository contentRepo;
    private final FeedbackMapper mapper;

    public FeedbackService(CommentaireRepository commentRepo,
                           LikeRepository likeRepo,
                           ContentRepository contentRepo,
                           FeedbackMapper mapper) {
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
        this.contentRepo = contentRepo;
        this.mapper = mapper;
    }

    public CommentResponse addComment(CommentRequest request) {
        // Récupération du contenu
        Integer contentIdInt = request.contentId().intValue();
        Content content = contentRepo.findById(contentIdInt)
                .orElseThrow(() -> new EntityNotFoundException("Contenu non trouvé"));

        // Création du commentaire
        Commentaire comment = new Commentaire();
        comment.setTexte(request.texte());
        comment.setAuteurId(request.userId()); // plus besoin d'entité User
        comment.setContenuCours(content);

        // Sauvegarde
        Commentaire saved = commentRepo.save(comment);

        // Mapping vers DTO de réponse
        return mapper.toCommentResponse(saved);
    }

    public List<CommentResponse> getCommentsByContent(Long contentId) {
        return commentRepo.findByContenuCoursId(contentId).stream()
                .map(mapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    public LikeResponse addLike(LikeRequest request) {
        // Récupération du contenu
        Integer contentIdInt = request.contentId().intValue();
        Content content = contentRepo.findById(contentIdInt)
                .orElseThrow(() -> new EntityNotFoundException("Contenu non trouvé"));

        // Création du Like
        Like like = new Like();
        like.setUserId(request.userId()); // plus besoin d'entité User
        like.setContenuCours(content);

        // Sauvegarde
        Like saved = likeRepo.save(like);

        // Mapping vers DTO de réponse
        return mapper.toLikeResponse(saved);
    }

    @Transactional
    public void removeLike(LikeRequest request) {
        likeRepo.deleteByUserIdAndContenuCoursId(request.userId(), request.contentId());
    }


    public long countLikesByContent(Long contentId) {
        return likeRepo.countByContenuCoursId(contentId);
    }

    // Compter les commentaires par contentId
    public long countCommentsByContent(Long contentId) {
        return commentRepo.countByContenuCoursId(contentId);
    }

}
