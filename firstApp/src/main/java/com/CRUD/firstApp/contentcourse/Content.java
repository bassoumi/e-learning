package com.CRUD.firstApp.contentcourse;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;

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


}
