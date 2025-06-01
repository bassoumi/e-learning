package com.CRUD.firstApp.notification;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByStudentIdAndReadFalse(int studentId);
}
