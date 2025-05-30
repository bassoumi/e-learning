package com.CRUD.firstApp.progression;

import org.springframework.stereotype.Component;

@Component
public class ProgressionMapper {

    public Progression toProgression(ProgressionRequest request) {
        Progression progression = new Progression();
        // Student and content should be set in the service layer based on IDs passed in the URL
        progression.setProgressionPercentage(request.progressionPercentage());
        progression.setLastAccessed(request.lastAccessed());
        progression.setStatus(request.status());
        return progression;
    }

    public ProgressionResponce toProgressionResponce(Progression progression) {
        return new ProgressionResponce(
                progression.getId(),
                progression.getStudent().getId(),
                progression.getContentEnCours().getId(),
                progression.getProgressionPercentage(),
                progression.getLastAccessed(),
                progression.getStatus()
        );
    }
}
