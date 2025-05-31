package com.CRUD.firstApp.progression;


import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "content_en_cours_id"})
)
@Entity
@Data
public class Progression {

    @Id
    @GeneratedValue()
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content contentEnCours;

    @Column(name = "progress_percentage", nullable = false)
    private Double progressionPercentage;

    @Column(name = "last_accessed", nullable = false)
    private LocalDateTime lastAccessed;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ProgressionStatus status;


}
