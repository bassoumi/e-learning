package com.CRUD.firstApp.agenda;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
    Optional<Agenda> findByStudent_IdAndCourse_Id(Integer studentId, Integer courseId);
}
