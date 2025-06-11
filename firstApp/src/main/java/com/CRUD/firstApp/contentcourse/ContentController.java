package com.CRUD.firstApp.contentcourse;


import com.CRUD.firstApp.video.VideoSummary;
import com.CRUD.firstApp.video.VideoSummaryResponse;
import com.CRUD.firstApp.video.VideoSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    private final ContentService contentService;
    private final VideoSummaryService summaryService;


    public ContentController(ContentService contentService, VideoSummaryService summaryService) {
        this.contentService = contentService;
        this.summaryService = summaryService;
    }

    @GetMapping
    public List<ContentResponce> getAllContents() {
        return contentService.getAllContents();
    }
    @GetMapping("/{id}")
    public ContentResponce getContentById(@PathVariable int id) {
        return contentService.getContentById(id);
    }

    @PostMapping
    public ContentResponce createContent(@RequestBody ContentRequest request) {
        return contentService.createContent(request);
    }

    @PutMapping("/{id}")
    public ContentResponce updateContent( @PathVariable  int id ,@RequestBody ContentRequest content) {
        return contentService.updateContent(id , content);
    }

    @GetMapping("name")
    public List<ContentResponce> getContentByName(@RequestParam String name) {
        return contentService.getContentByName(name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContentById(@PathVariable int id) {
         contentService.deleteContentById(id);
        return  ResponseEntity.ok("Content deleted successfully");
    }
    @PostMapping("/{id}/summarize")
    public VideoSummaryResponse summarizeContent(@PathVariable Integer id) {
        VideoSummary vs = summaryService.generateAndSaveSummary(id);
        return new VideoSummaryResponse(vs);
    }

    @GetMapping("/{id}/summary")
    public VideoSummaryResponse getContentSummary(@PathVariable int id) {
        VideoSummary vs = summaryService.getLatestSummaryByContentId(id);
        return new VideoSummaryResponse(vs);
    }


}
