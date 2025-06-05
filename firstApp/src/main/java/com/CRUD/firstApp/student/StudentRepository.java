package com.CRUD.firstApp.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository <Student, Integer> {

    List<Student> findByFirstName(String firstName);

    Optional<Student> findByEmail(String email);

    @Query("select s from Student s " +
            "left join fetch s.instructors i " +
            "where s.id = :id")
    Optional<Student> findByIdWithInstructors(@Param("id") int id);


    @Query("SELECT FUNCTION('TO_CHAR', s.createdAt, 'YYYY-MM') AS month, COUNT(s) AS count " +
            "FROM Student s GROUP BY FUNCTION('TO_CHAR', s.createdAt, 'YYYY-MM') ORDER BY month")
    List<Object[]> getMonthlyRegistrationStats();


}
