package com.CRUD.firstApp.student;


import com.CRUD.firstApp.courses.Courses;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name="student")
public class Student {

    @Id
    @GeneratedValue
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String email;
    private String phone;
    @Embedded
    private Address address;

    @ManyToMany(mappedBy = "students")
    private Set<Courses> courses = new HashSet<>();


}
