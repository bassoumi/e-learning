package com.CRUD.firstApp.video;


import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class VideoSummaryService {
    private final VideoSummaryRepository repo;
    private final ContentRepository contentRepo;
    private final WebClient webClient;

    public VideoSummaryService(VideoSummaryRepository repo,
                               ContentRepository contentRepo,
                               WebClient.Builder builder) {
        this.repo = repo;
        this.contentRepo = contentRepo;
        this.webClient = builder.baseUrl("http://localhost:5001").build();
    }

    @Transactional
    public VideoSummary generateAndSaveSummary(Integer contentId) {
        System.out.println("🔍 Start generating summary for content ID: " + contentId);

        // 1) Retrieve content
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> {
                    System.out.println("❌ Content not found for ID: " + contentId);
                    return new EntityNotFoundException("Content not found");
                });
        System.out.println("✅ Content found: " + content.getTitle() + " | URL: " + content.getVideoUrl());

        // 2) Call Python microservice
        System.out.println("📡 Calling Python microservice...");
        Map<String, String> resp = webClient.post()
                .uri("/api/summary")
                .bodyValue(Map.of("youtubeUrl", content.getVideoUrl()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,String>>() {})
                .block();

        if (resp == null || !resp.containsKey("summary")) {
            System.out.println("⚠️ Response is null or missing 'summary' key: " + resp);
            throw new IllegalStateException("Invalid response from summary service");
        }

        String summaryText = resp.get("summary");
        System.out.println("📄 Summary received: " + summaryText.substring(0, Math.min(100, summaryText.length())) + "...");

        // 3) Find existing summary or create new
        VideoSummary vs = repo
                .findFirstByContentOrderByIdDesc(content)
                .orElseGet(() -> {
                    System.out.println("➕ Creating new VideoSummary entry");
                    VideoSummary v = new VideoSummary();
                    v.setContent(content);
                    v.setVideoUrl(content.getVideoUrl());
                    return v;
                });
        System.out.println("🧠 Final summary to save (length " + summaryText.length() + "): " + summaryText);

        // 4) Set and save
        // 4) Set and save
        vs.setSummaryText(summaryText);
        VideoSummary saved = repo.save(vs);
        System.out.println("💾 VideoSummary saved with ID: " + saved.getId());

        return saved;
    }

    @Transactional
    public VideoSummary getLatestSummaryByContentId(int contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found"));

        return repo.findFirstByContentOrderByIdDesc(content)
                .orElseThrow(() -> new EntityNotFoundException("No summary found for content"));
    }


}
