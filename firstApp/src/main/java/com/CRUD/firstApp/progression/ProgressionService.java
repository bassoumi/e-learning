package com.CRUD.firstApp.progression;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProgressionService {
    private final ProgressionRepository progressionRepository;
    private final ProgressionMapper progressionMapper;

    public List<ProgressionResponce> getAllProgression() {
        return progressionRepository.findAll()
                .stream()
                .map(progressionMapper::toProgressionResponce)
                .collect(Collectors.toList());
    }

    public ProgressionResponce addProgression(ProgressionRequest request) {
        Progression progression = progressionMapper.toProgression(request);
        progression = progressionRepository.save(progression);
        return progressionMapper.toProgressionResponce(progression);
    }

    public ProgressionResponce getProgressionById(int id) {
        var progressionExsist = progressionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progression not found with id: " + id));
        return progressionMapper.toProgressionResponce(progressionExsist);
    }

    public ProgressionResponce updateProgression(int id, ProgressionRequest request) {
        var progressionExsist = progressionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progression not found with id: " + id));

        if (request.student() != null) {
            progressionExsist.setStudent(request.student());
        }

        if (request.contentEnCours() != null) {
            progressionExsist.setContentEnCours(request.contentEnCours());
        }

        if (request.progressionPercentage() != null) {
            progressionExsist.setProgressionPercentage(request.progressionPercentage());
        }

        if (request.lastAccessed() != null) {
            progressionExsist.setLastAccessed(request.lastAccessed());
        }

        if (request.status() != null) {
            progressionExsist.setStatus(request.status());
        }
        progressionRepository.save(progressionExsist);
        return progressionMapper.toProgressionResponce(progressionExsist);
    }

    public void deleteProgression(int id) {
         progressionRepository.deleteById(id);
    }
}
