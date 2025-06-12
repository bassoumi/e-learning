package com.CRUD.firstApp.video;


import com.CRUD.firstApp.contentcourse.Content;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video_summaries")
@Getter
@Setter
public class VideoSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String videoUrl;

    @Lob
    @Column(nullable = false)
    private String summaryText;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private Content content;
}
