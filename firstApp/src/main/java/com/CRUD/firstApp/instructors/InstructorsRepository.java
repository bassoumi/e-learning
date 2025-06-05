package com.CRUD.firstApp.instructors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

public interface InstructorsRepository extends JpaRepository<Instructors, Integer> {
    List<Instructors> findByFirstName(String firstName);

    Optional<Instructors> findByEmail(String email);

    @Query("""
    SELECT new com.CRUD.firstApp.instructors.PopularInstructorRecord(
        i.id,
        CONCAT(i.firstName, ' ', i.lastName),
        COUNT(s.id)
    )
    FROM Instructors i
    JOIN i.students s
    GROUP BY i.id, i.firstName, i.lastName
    ORDER BY COUNT(s.id) DESC
""")
    List<PopularInstructorRecord> findTopPopularInstructors(Pageable pageable);

    @Query("""
        SELECT COUNT(s.id)
        FROM Instructors i
        JOIN i.students s
        WHERE i.id = :instructorId
    """)
    Long countSubscribersByInstructorId(@Param("instructorId") Integer instructorId);

}