package com.CRUD.firstApp.admin;

import com.CRUD.firstApp.courses.CoursesRepository;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import com.CRUD.firstApp.instructors.PopularInstructorRecord;
import com.CRUD.firstApp.student.StudentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminService {

    private final StudentRepository StudentRepository;
    private final CoursesRepository CoursesRepository;
    private final InstructorsRepository InstructorsRepository;

    public AdminService(StudentRepository StudentRepository, CoursesRepository CoursesRepository, InstructorsRepository instructorsRepository) {
        this.StudentRepository = StudentRepository;
        this.CoursesRepository = CoursesRepository;
        InstructorsRepository = instructorsRepository;
    }

    public long getUserNumber() {
        return StudentRepository.count();
    }

    public long getCourseNumber() {
        return CoursesRepository.count();

    }

    public Map<String, Long> getMonthlyRegistrationStats() {
        List<Object[]> results = StudentRepository.getMonthlyRegistrationStats();
        Map<String, Long> stats = new LinkedHashMap<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            Long count = (Long) row[1];

            // Vérifie que la clé n'est pas nulle
            if (month != null) {
                stats.put(month, count != null ? count : 0L); // par sécurité, si count est nul
            }
        }

        return stats;
    }


    public long getInstructorNumber() {
        return InstructorsRepository.count();
    }

    public List<PopularInstructorRecord> getTopInstructors(int topN) {
        // PageRequest.of(...) renvoie un PageRequest qui implémente Pageable
        Pageable pageable = PageRequest.of(0, topN);
        return InstructorsRepository.findTopPopularInstructors(pageable);
    }

}