package com.CRUD.firstApp.feedback;

import com.CRUD.firstApp.contentcourse.Content;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "commentaires")
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String texte;

    @Column(name = "auteur_id", nullable = false)
    private Integer auteurId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "contenu_id")
    private Content contenuCours;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();


}
