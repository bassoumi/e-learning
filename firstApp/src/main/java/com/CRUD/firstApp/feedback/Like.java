package com.CRUD.firstApp.feedback;


import com.CRUD.firstApp.contentcourse.Content;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "contenu_id")
    private Content contenuCours;

    @Column(nullable = false)
    private LocalDateTime dateLike = LocalDateTime.now();


}
