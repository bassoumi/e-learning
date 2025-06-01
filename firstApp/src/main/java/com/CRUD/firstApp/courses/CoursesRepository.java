package com.CRUD.firstApp.courses;

import com.CRUD.firstApp.instructors.Instructors;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    List<Courses> findByCategorie_Id(int categoryId);    List<Courses> findByTitle(String title);

    @EntityGraph(attributePaths = "instructor")
    @Query("SELECT c FROM Courses c")
    List<Courses> findAllWithInstructors();

    List<Courses> findByTitleContainingIgnoreCase(String title);

    List<Courses> findByInstructor_Id(int instructorId); // ✅


}
