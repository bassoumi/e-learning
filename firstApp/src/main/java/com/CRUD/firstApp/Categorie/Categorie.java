// Categorie.java
package com.CRUD.firstApp.Categorie;

import com.CRUD.firstApp.courses.Courses;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "Categorie")
// <-- Une fois qu’une catégorie est sérialisée une première fois,
// Jackson n’imprimera que { "id": … } à la ré-occurrence.
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Categorie {
    @Id
    @GeneratedValue
    private int id;

    private String nom;
    private String description;
    private String CoverCategoryimage;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    // Courses est déjà annoté avec @JsonIdentityInfo, donc on n’a pas besoin d’ajouter
    // d’autre annotation ici : les boucles seront gérées grâce à l’ID.
    private List<Courses> courses = new ArrayList<>();
}
