package com.CRUD.firstApp.agenda;


import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate studyDate;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Courses course;

    private LocalDateTime startedAt;              // When the student started studying that day
    private LocalDateTime lastProgressionUpdate;  // Last update time that day

    private String eventType;
}
