package com.CRUD.firstApp.quiz;


import com.CRUD.firstApp.courses.CourseResponse;
import com.CRUD.firstApp.courses.CoursesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final CoursesRepository CoursesRepository;


    public QuizService(QuizRepository quizRepository, QuizMapper quizMapper, CoursesRepository coursesRepository) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
        CoursesRepository = coursesRepository;
    }


    public List<QuizResponse> getAllQuiz() {
        return quizRepository.findAll()
                .stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    public QuizResponse getQuizById(int id) {
        var exsistQuiz = quizRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Quiz not found with id " + id));
        return quizMapper.toResponse(exsistQuiz);
    }

    public List<QuizResponse> findByTitle(String title) {
        var exsistQuiz = quizRepository.findByTitle(title);
        if (exsistQuiz.isEmpty()) {
            throw new RuntimeException("Quiz not found with title " + title);
        }
        return exsistQuiz
                .stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Transactional
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        Quiz quizEntity = quizMapper.toEntity(quizRequest);

        if (quizRequest.courseId() != null) {
            CoursesRepository.findById(quizRequest.courseId())
                    .ifPresent(quizEntity::setCourse);
        }

        quizRepository.save(quizEntity);
        return quizMapper.toResponse(quizEntity);
    }



    @Transactional
    public QuizResponse updateQuiz(int id, QuizRequest request) {
        Quiz quizEntity = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id " + id));

        // 1. Update title if given
        if (StringUtils.hasText(request.title())) {
            quizEntity.setTitle(request.title());
        }

        // 2. Replace questions wholesale if provided
        if (request.questions() != null) {
            List<QuizQuestions> updatedQs = request.questions().stream()
                    .map(qr -> {
                        QuizQuestions q = new QuizQuestions();
                        q.setText(qr.text());
                        q.setOptions(new ArrayList<>(qr.options()));
                        q.setAnswer(qr.answer());
                        return q;
                    })
                    .toList();
            quizEntity.setQuestions(updatedQs);
        }

        // 3. Update course association
        if (request.courseId() != null) {
            CoursesRepository.findById(request.courseId())
                    .ifPresent(quizEntity::setCourse);
        }

        // 4. Save changes and return DTO
        quizRepository.save(quizEntity);
        return quizMapper.toResponse(quizEntity);
    }




    public void deleteQuiz(int id) {
        quizRepository.deleteById(id);
    }
}
