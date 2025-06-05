package com.CRUD.firstApp.progression;


import com.CRUD.firstApp.agenda.AgendaService;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentRepository;
import com.CRUD.firstApp.courses.*;
import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProgressionService {
    private final ProgressionRepository progressionRepository;
    private final ProgressionMapper progressionMapper;
    private final ContentRepository contentRepository;
    private final StudentRepository StudentRepository;
    private final CoursesRepository courseRepository;
    private final AgendaService agendaService;

    public List<ProgressionResponce> getAllProgression() {
        return progressionRepository.findAll()
                .stream()
                .map(progressionMapper::toProgressionResponce)
                .collect(Collectors.toList());
    }


    public ProgressionResponce getProgressionById(int id) {
        var progressionExsist = progressionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progression not found with id: " + id));
        return progressionMapper.toProgressionResponce(progressionExsist);
    }


    public void deleteProgression(int id) {
        progressionRepository.deleteById(id);
    }

    public List<ProgressionResponce> listByStudent(Integer studentId) {
        return progressionRepository
                .findAllByStudent_Id(studentId)
                .stream()
                .map(progressionMapper::toProgressionResponce)
                .toList();
    }
    public ProgressionResponce addProgression(
            Integer studentId,
            Integer contentId,
            ProgressionRequest req
    ) {
        Student student = StudentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));

        // 1) Toujours appeler logCourseProgress → crée ou met à jour l’agenda d’aujourd’hui
        Courses course = content.getCourse();
        agendaService.logCourseProgress(student, course);

        // 2) Récupérer / créer l’objet Progression comme d’habitude
        Optional<Progression> existing = progressionRepository
                .findByStudent_IdAndContentEnCours_Id(studentId, contentId);
        Progression p;
        if (existing.isPresent()) {
            p = existing.get();
        } else {
            p = new Progression();
            p.setStudent(student);
            p.setContentEnCours(content);
        }

        // 3) Mettre à jour les champs de progression
        if (req.progressionPercentage() != null) {
            p.setProgressionPercentage(req.progressionPercentage());
        }
        if (req.lastAccessed() != null) {
            p.setLastAccessed(req.lastAccessed());
        } else {
            p.setLastAccessed(LocalDateTime.now());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }

        // 4) Sauver la progression
        p = progressionRepository.save(p);

        return progressionMapper.toProgressionResponce(p);
    }


    public ProgressionResponce updateProgression(
            Integer studentId,
            Integer contentId,
            ProgressionRequest req
    ) {
        Progression p = progressionRepository
                .findByStudent_IdAndContentEnCours_Id(studentId, contentId)
                .orElseThrow(() -> new RuntimeException("No progression found"));

        // Mettre à jour les champs reçus
        if (req.progressionPercentage() != null) {
            p.setProgressionPercentage(req.progressionPercentage());
        }
        if (req.lastAccessed() != null) {
            p.setLastAccessed(req.lastAccessed());
        } else {
            p.setLastAccessed(LocalDateTime.now());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }

        // Sauvegarder la progression
        p = progressionRepository.save(p);

        // Ensuite, log la progression du jour (UPDATE)
        Courses course = p.getContentEnCours().getCourse();
        Student student = p.getStudent();
        agendaService.logCourseProgress(student, course);

        return progressionMapper.toProgressionResponce(p);
    }




    public List<CourseResponseProgress> listInProgressCoursesWithLastViewed(Integer studentId) {
        List<Integer> courseIds = progressionRepository
                .findDistinctCourseIdsByStudentIdAndStatusNot(studentId, ProgressionStatus.COMPLETED);

        List<Courses> courses = courseRepository.findAllById(courseIds);

        return courses.stream().map(course -> {
            Optional<Progression> lastProgress = progressionRepository
                    .findTopByStudentIdAndContentEnCoursCourseIdOrderByLastAccessedDesc(studentId, course.getId());

            String lastTitle = lastProgress.map(p -> p.getContentEnCours().getTitle()).orElse("Unknown");
            Integer lastId = lastProgress.map(p -> p.getContentEnCours().getId()).orElse(null);

            Double averageProgression = progressionRepository.calculateAverageProgression(studentId, course.getId());
            if (averageProgression == null) averageProgression = 0.0;

            return new CourseResponseProgress(
                    course.getId(),
                    course.getTitle(),
                    course.getShortDescription(),
                    course.getLevel(),
                    course.getLanguage(),
                    course.getCoverImage(),
                    course.getCategorie().getNom(),
                    course.getCategorie().getId(),
                    course.getInstructor().getId(),
                    course.getInstructor().getFirstName(),
                    lastTitle,
                    lastId,
                    averageProgression
            );
        }).toList();
    }



    public double getGlobalCompletionRate() {
        long total = progressionRepository.count();
        if (total == 0) {
            return 0.0;
        }
        long completed = progressionRepository.countByStatus(ProgressionStatus.COMPLETED); // <-- passez l'enum, pas une chaîne
        double rate = (completed * 100.0) / total;
        return Math.round(rate * 100.0) / 100.0;
    }


}
