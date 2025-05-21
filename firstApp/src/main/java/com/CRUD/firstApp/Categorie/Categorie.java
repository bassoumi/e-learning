package com.CRUD.firstApp.Categorie;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Categorie")

public class Categorie {
    @Id
    @GeneratedValue
    private int id;

    private String nom;

    private String description;

    private String slug;

    private String CoverCategoryimage;


    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @OneToMany(mappedBy = "categorie" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Courses> courses = new ArrayList<>();

}
