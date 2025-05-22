package com.CRUD.firstApp.contentcourse;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
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

}
