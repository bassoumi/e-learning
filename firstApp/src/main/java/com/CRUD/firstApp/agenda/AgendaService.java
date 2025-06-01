package com.CRUD.firstApp.agenda;

import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.courses.CoursesRepository;
import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final StudentRepository studentRepository;
    private final CoursesRepository courseRepository;

    public AgendaService(AgendaRepository agendaRepository, StudentRepository studentRepository, CoursesRepository courseRepository) {
        this.agendaRepository = agendaRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }


    public void saveCourseStartDate(Integer studentId, Integer courseId) {
        Optional<Agenda> existing = agendaRepository.findByStudent_IdAndCourse_Id(studentId, courseId);

        if (existing.isEmpty()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Courses course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            Agenda entry = new Agenda();
            entry.setStudent(student);
            entry.setCourse(course);
            entry.setStartedAt(LocalDateTime.now());
            entry.setEventType("START");
            agendaRepository.save(entry);
        }
    }
}
