package com.CRUD.firstApp.contentcourse;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {
    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    public ContentService(ContentRepository contentRepository, ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    public List<ContentResponce> getAllContents() {
       return contentRepository.findAll()
                .stream()
                .map(contentMapper::toResponce)
                .collect(Collectors.toList());
    }

    public ContentResponce getContentById(int id) {
        var contentExiste = contentRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found with id: " + id));
        return contentMapper.toResponce(contentExiste);
    }


    public ContentResponce updateContent(int id, ContentRequest request) {
        var contentExiste = contentRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found with id: " + id));

        if (StringUtils.hasText(request.title())){
            contentExiste.setTitle(request.title());
        }
        if (StringUtils.hasText(request.description())){
            contentExiste.setDescription(request.description());
        }
        if (StringUtils.hasText(request.videoUrl())){
            contentExiste.setVideoUrl(request.videoUrl());
        }
        if (request.orderContent() != null) {
            contentExiste.setOrderContent(request.orderContent());
        }

        contentRepository.save(contentExiste);
        return contentMapper.toResponce(contentExiste);

    }

    public List<ContentResponce> getContentByName(String name) {
        return  contentRepository.findByTitle(name)
                .stream()
                .map(contentMapper::toResponce)
                .collect(Collectors.toList());
    }

    public void deleteContentById(int id) {
        contentRepository.deleteById(id);
    }

    public ContentResponce createContent(ContentRequest request) {
        var contentEntity = contentMapper.toContent(request);
        contentRepository.save(contentEntity);
        return contentMapper.toResponce(contentEntity);
    }
}
