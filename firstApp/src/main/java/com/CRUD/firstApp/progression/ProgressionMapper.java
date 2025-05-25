package com.CRUD.firstApp.progression;

import org.springframework.stereotype.Component;

@Component
public class ProgressionMapper {

    public Progression toProgression(ProgressionRequest request){
        Progression progression = new Progression();
        progression.setStudent(request.student());
        progression.setContentEnCours(request.contentEnCours());
        progression.setProgressionPercentage(request.progressionPercentage());
        progression.setLastAccessed(request.lastAccessed());
        progression.setStatus(request.status());
        return progression;
    }

    public ProgressionResponce toProgressionResponce(Progression progression){
        return new ProgressionResponce(
                progression.getStudent().getId(),
                progression.getContentEnCours().getId(),
                progression.getProgressionPercentage(),
                progression.getLastAccessed(),
                progression.getStatus()
        );

    }
}
