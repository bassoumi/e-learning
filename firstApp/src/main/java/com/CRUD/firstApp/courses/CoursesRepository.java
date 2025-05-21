package com.CRUD.firstApp.courses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    List<Courses> findByTitle(String title);

}
