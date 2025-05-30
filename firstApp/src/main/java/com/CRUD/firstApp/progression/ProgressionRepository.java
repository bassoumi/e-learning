package com.CRUD.firstApp.progression;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Integer> {
    Optional<Progression> findByStudentIdAndContentEnCoursId(
            Integer studentId,
            Integer contentId);

    List<Progression> findAllByStudentId(Integer studentId);

    @Query("select distinct cEnCours.course.id " +
            "from Progression p " +
            "join p.contentEnCours cEnCours " +
            "where p.student.id = :studentId " +
            "and p.status <> :completedStatus")
    List<Integer> findDistinctCourseIdsByStudentIdAndStatusNot(
            @Param("studentId") Integer studentId,
            @Param("completedStatus") ProgressionStatus completedStatus
    );

    Optional<Progression> findTopByStudentIdAndContentEnCoursCourseIdOrderByLastAccessedDesc(Integer studentId, Integer courseId);
}