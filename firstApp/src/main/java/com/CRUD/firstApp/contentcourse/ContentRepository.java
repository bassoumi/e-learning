package com.CRUD.firstApp.contentcourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findByTitle(String title);

    @Query("SELECT COUNT(c) FROM Content c WHERE c.course.id = :courseId")
    long countByCourseId(@Param("courseId") Integer courseId);

}
