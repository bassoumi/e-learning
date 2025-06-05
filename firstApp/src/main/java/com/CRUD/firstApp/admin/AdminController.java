package com.CRUD.firstApp.admin;


import com.CRUD.firstApp.instructors.PopularInstructorRecord;
import com.CRUD.firstApp.progression.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/v1/dashboard")
public class AdminController {


    private final AdminService adminService;
    private final ProgressionService progressionService;

    public AdminController(AdminService adminService, ProgressionService progressionService) {
        this.adminService = adminService;
        this.progressionService = progressionService;
    }

    @GetMapping("/courses/count")
    public ResponseEntity<Long> getCourseNumber() {
        long count = adminService.getCourseNumber();
        return ResponseEntity.ok(count);
    }



    @GetMapping("/student/count")
    public ResponseEntity<Long> getUserNumber() {
        long count = adminService.getUserNumber();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/instructors/count")
    public ResponseEntity<Long> getInstructorNumber() {
        long count = adminService.getInstructorNumber();
        return ResponseEntity.ok(count);
    }



    @GetMapping("/completion-rate")
    public ResponseEntity<Double> getGlobalCompletionRate() {
        double rate = progressionService.getGlobalCompletionRate();
        return ResponseEntity.ok(rate);
    }


    @GetMapping("/registrations/monthly")
    public ResponseEntity<Map<String, Long>> getMonthlyRegistrations() {
        return ResponseEntity.ok(adminService.getMonthlyRegistrationStats());
    }

    @GetMapping("/top-instructors")
    public List<PopularInstructorRecord> getTopInstructors(@RequestParam(defaultValue = "3") int limit) {
        return adminService.getTopInstructors(limit);
    }

}
