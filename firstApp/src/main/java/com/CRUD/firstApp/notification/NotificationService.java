package com.CRUD.firstApp.notification;

import com.CRUD.firstApp.courses.Courses;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.student.Student;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void notifyNewCourseToSubscribers(Courses newCourse) {
        // 1) Récupérer l’instructeur du cours, puis ses étudiants abonnés
        Instructors instructor = newCourse.getInstructor();
        Set<Student> subscribers = instructor.getStudents(); // attention à avoir le FetchType adéquat

        // 2) Pour chaque étudiant, créer la Notification
        for (Student student : subscribers) {
            Notification n = new Notification();
            n.setStudent(student);
            n.setCourse(newCourse);
            n.setRead(false);
            n.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
    }


    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(int studentId) {
        return notificationRepository.findAllByStudentIdAndReadFalse(studentId);
    }


    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification introuvable (ID: " + notificationId + ")"
                ));
        n.setRead(true);
        notificationRepository.save(n);
    }



    @Transactional
    public void markAsReadByStudentAndCourse(int studentId, int courseId) {
        List<Notification> list = notificationRepository
                .findAllByStudentIdAndReadFalse(studentId)
                .stream()
                .filter(n -> n.getCourse().getId() == courseId)
                .toList();
        for (Notification n : list) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    @Transactional(readOnly = true)
    public Notification getById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification introuvable (ID: " + notificationId + ")"
                ));
    }

}
