package com.CRUD.firstApp.progression;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/progression")
@RestController
@AllArgsConstructor
public class ProgressionContoller {

    private final ProgressionService progressionService;

    @GetMapping
    public List<ProgressionResponce> getAllProgression() {
        return progressionService.getAllProgression();
    }

    @PostMapping
    public  ProgressionResponce addProgression(@RequestBody ProgressionRequest request) {
        return progressionService.addProgression(request);
    }

    @GetMapping("/{id}")
    public ProgressionResponce getProgressionById(@PathVariable int id) {
        return progressionService.getProgressionById(id);

    }

    @PutMapping("/{id}")
    public ProgressionResponce updateProgression(@PathVariable int id, @RequestBody ProgressionRequest request) {
        return progressionService.updateProgression(id,request);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id) {
         progressionService.deleteProgression(id);
         return ResponseEntity.ok("Progression deleted");

    }

}
