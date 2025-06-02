package com.CRUD.firstApp.agenda;

import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.courses.CoursesRepository;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final StudentRepository studentRepository;


    public AgendaService(AgendaRepository agendaRepository, StudentRepository studentRepository) {
        this.agendaRepository = agendaRepository;

        this.studentRepository = studentRepository;
    }



    public void logCourseProgress(Student student, Courses course) {
        LocalDate today = LocalDate.now();

        // 1) Vérifier s’il existe déjà une entrée pour le JOUR (= today)
        Optional<Agenda> existingToday = agendaRepository
                .findByStudentAndCourseAndStudyDate(student, course, today);

        if (existingToday.isPresent()) {
            // → On met à jour la même journée
            Agenda agenda = existingToday.get();
            agenda.setLastProgressionUpdate(LocalDateTime.now());
            agenda.setEventType("UPDATE");
            agendaRepository.save(agenda);
        } else {
            // → Nouvelle entrée pour AUJOURD’HUI :
            //    déterminer si c’est la toute première fois ever
            boolean isFirstEver = agendaRepository.existsByStudentAndCourse(student, course);

            Agenda newLog = new Agenda();
            newLog.setStudent(student);
            newLog.setCourse(course);
            newLog.setStudyDate(today);
            newLog.setStartedAt(LocalDateTime.now());
            newLog.setLastProgressionUpdate(LocalDateTime.now());
            // Si aucun enregistrement existant pour ce (student, course), c’est "START", sinon "UPDATE"
            newLog.setEventType(isFirstEver ? "UPDATE" : "START");
            agendaRepository.save(newLog);
        }
    }
    public List<AgendaEntryDto> getAgendaEntriesForStudent(Integer studentId) {
        // 1) Vérifier que l'étudiant existe
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable (ID: " + studentId + ")"
                ));

        // 2) Parcourir toutes les entrées d'agenda pour cet étudiant
        return agendaRepository.findAllByStudentId(studentId)
                .stream()
                .map(agenda -> {
                    // On récupère le cours lié à cette entrée d'agenda
                    Courses course = agenda.getCourse();

                    // Titre du cours
                    String title = course.getTitle();
                    String coursImage = course.getCoverImage();


                    // On récupère l'instructeur du cours (relation @ManyToOne)
                    Instructors instructor = course.getInstructor();
                    String instructorName;
                    if (instructor != null) {
                        // Exemple : "Prénom Nom"
                        instructorName = instructor.getFirstName() + " " + instructor.getLastName();
                    } else {
                        instructorName = "Instructeur inconnu";
                    }

                    return new AgendaEntryDto(
                            agenda.getId(),
                            agenda.getStudyDate(),
                            agenda.getStartedAt(),
                            agenda.getLastProgressionUpdate(),
                            agenda.getEventType(),
                            course.getId(),
                            title,
                            instructorName,
                            coursImage
                    );
                })
                .collect(Collectors.toList());
    }


}
