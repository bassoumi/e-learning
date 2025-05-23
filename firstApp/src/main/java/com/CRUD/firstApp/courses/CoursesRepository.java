package com.CRUD.firstApp.courses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    List<Courses> findByTitle(String title);

}
