package com.CRUD.firstApp.agenda.PersonalEvent;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalEventRepository extends JpaRepository<PersonalEvent, Integer> {
    List<PersonalEvent> findAllByStudentId(Integer studentId);

}
