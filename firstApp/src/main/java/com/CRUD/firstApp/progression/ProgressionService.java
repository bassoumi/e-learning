package com.CRUD.firstApp.progression;


import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentRepository;
import com.CRUD.firstApp.courses.CourseResponse;
import com.CRUD.firstApp.courses.CourseResponseProgress;
import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.courses.CoursesRepository;
import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
                .findAllByStudentId(studentId)
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

        // Vérifier si une progression existe déjà
        Optional<Progression> existing = progressionRepository.findByStudentIdAndContentEnCoursId(studentId, contentId);
        Progression p;
        if (existing.isPresent()) {
            // Mise à jour
            p = existing.get();
        } else {
            // Création
            p = new Progression();
            p.setStudent(student);
            p.setContentEnCours(content);
        }

        if (req.progressionPercentage() != null) {
            p.setProgressionPercentage(req.progressionPercentage());
        }
        if (req.lastAccessed() != null) {
            p.setLastAccessed(req.lastAccessed());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }

        p = progressionRepository.save(p);
        return progressionMapper.toProgressionResponce(p);
    }



    public ProgressionResponce updateProgression(
            Integer studentId,
            Integer contentId,
            ProgressionRequest req
    ) {
        Progression p = progressionRepository
                .findByStudentIdAndContentEnCoursId(studentId, contentId)
                .orElseThrow(() -> new RuntimeException(
                        "No progression for student=" + studentId + " content=" + contentId));

        if (req.progressionPercentage() != null) {
            // Stocke la valeur brute qu’on reçoit dans req, sans l’additionner à l’ancienne.
            p.setProgressionPercentage(req.progressionPercentage());
        }

        if (req.lastAccessed() != null) {
            p.setLastAccessed(req.lastAccessed());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }

        progressionRepository.save(p);
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




}
