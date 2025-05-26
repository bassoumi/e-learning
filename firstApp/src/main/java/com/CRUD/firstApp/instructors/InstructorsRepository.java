package com.CRUD.firstApp.instructors;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstructorsRepository extends JpaRepository<Instructors, Integer> {
    List<Instructors> findByFirstName(String firstName);

    Optional<Instructors> findByEmail(String email);
}
