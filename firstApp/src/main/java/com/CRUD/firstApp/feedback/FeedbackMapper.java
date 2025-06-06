package com.CRUD.firstApp.feedback;

import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {


    private final StudentRepository studentRepo;

    public FeedbackMapper(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }  // ← fermeture du constructeur ajoutée ici

    public CommentResponse toCommentResponse(Commentaire comment) {
        // 1. Récupération de l’ID de l’étudiant
        Integer studentId = comment.getAuteurId();

        // 2. Recherche de l’étudiant en base pour obtenir prénom + nom de famille
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        String fullName = student.getFirstName() + " " + student.getLastName();

        // 3. Construction du DTO en passant firstName et lastName
        return new CommentResponse(
                comment.getId(),
                comment.getTexte(),
                fullName,             // nom de famille
                comment.getDateCreation()
        );
    }

    public LikeResponse toLikeResponse(Like like) {

        int contenuIdInt = like.getContenuCours().getId();
        Long contenuIdEnLong = (long) contenuIdInt;
        return new LikeResponse(
                like.getId(),
                like.getUserId(),
                contenuIdEnLong,  // Pas besoin de Long.valueOf() si getId() renvoie déjà un Long
                like.getDateLike()
        );
    }
}