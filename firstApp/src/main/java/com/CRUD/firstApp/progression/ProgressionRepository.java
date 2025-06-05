package com.CRUD.firstApp.progression;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Integer> {


    Optional<Progression> findByStudent_IdAndContentEnCours_Id(Integer studentId, Integer contentId);


    List<Progression> findAllByStudent_Id(Integer studentId);

    long countByStatus(ProgressionStatus status);



    @Query("""
    SELECT DISTINCT p.contentEnCours.course.id
    FROM Progression p
    WHERE p.student.id = :studentId
      AND p.status <> :completedStatus
""")
    List<Integer> findDistinctCourseIdsByStudentIdAndStatusNot(
            @Param("studentId") Integer studentId,
            @Param("completedStatus") ProgressionStatus completedStatus
    );


    Optional<Progression> findTopByStudentIdAndContentEnCoursCourseIdOrderByLastAccessedDesc(Integer studentId, Integer courseId);




    @Query("""
    SELECT SUM(p.progressionPercentage) 
    FROM Progression p 
    WHERE p.student.id = :studentId 
      AND p.contentEnCours.course.id = :courseId
""")
    Double calculateAverageProgression(@Param("studentId") Integer studentId,
                                       @Param("courseId") Integer courseId);






}