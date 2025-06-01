package com.CRUD.firstApp.agenda;


import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Courses course;

    private LocalDateTime startedAt;

    private LocalDateTime lastProgressionUpdate;

    // Optionnel : pour gérer les rappels ou événements globaux plus tard
    private String eventType;
}
