package com.CRUD.firstApp.agenda;


import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
    List<Agenda> findAllByStudentId(Integer studentId);
    Optional<Agenda> findByStudentAndCourseAndStudyDate(
            Student student,
            Courses course,
            LocalDate studyDate
    );

    // ← La ligne à rajouter :
    boolean existsByStudentAndCourse(Student student, Courses course);
}
