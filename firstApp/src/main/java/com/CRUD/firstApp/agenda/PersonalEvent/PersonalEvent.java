package com.CRUD.firstApp.agenda.PersonalEvent;


import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "personal_event")
public class PersonalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Titre de l’événement (obligatoire)
    @Column(nullable = false)
    private String title;

    // Description libre (optionnelle)
    private String description;

    // Date et heure de l’événement
    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    // L’étudiant propriétaire de cet événement
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private Student student;
}