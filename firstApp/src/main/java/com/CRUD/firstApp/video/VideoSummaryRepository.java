package com.CRUD.firstApp.video;

import com.CRUD.firstApp.contentcourse.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoSummaryRepository extends JpaRepository<VideoSummary, Long> {
    Optional<VideoSummary> findFirstByContentOrderByIdDesc(Content content);

}
