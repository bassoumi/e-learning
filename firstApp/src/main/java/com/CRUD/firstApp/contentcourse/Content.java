package com.CRUD.firstApp.contentcourse;


import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.feedback.Commentaire;
import com.CRUD.firstApp.feedback.Like;
import com.CRUD.firstApp.video.VideoSummary;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Content {

    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String description;
    private String videoUrl;

    @Column(unique = true)
    private Integer orderContent;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Courses course;


    @OneToMany(mappedBy = "contenuCours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "contenuCours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();


    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private VideoSummary videoSummary;



}
