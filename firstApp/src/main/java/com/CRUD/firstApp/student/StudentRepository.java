package com.CRUD.firstApp.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository <Student, Integer> {

    List<Student> findByFirstName(String firstName);

    Optional<Student> findByEmail(String email);

}
