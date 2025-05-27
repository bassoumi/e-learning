package com.CRUD.firstApp.quiz;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;


    public QuizService(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
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

    public QuizResponse createQuiz(QuizRequest quizRequest) {
        var QuizToEntity = quizMapper.toEnity(quizRequest);
         quizRepository.save(QuizToEntity);
        return quizMapper.toResponse(QuizToEntity);

    }

    public QuizResponse updateQuiz(int id, QuizRequest request) {
        var quizEntity = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id " + id));

        if (StringUtils.hasText(request.title())) {
            quizEntity.setTitle(request.title());
        }
        if (request.questions() != null && !request.questions().isEmpty()) {
            quizEntity.setQuestions(request.questions());
        }
        if (request.answers() != null && !request.answers().isEmpty()) {
            quizEntity.setAnswers(request.answers());
        }
        if (request.options() != null && !request.options().isEmpty()) {
            // flatten List<List<String>> → List<String>
            List<String> flat = request.options().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            quizEntity.setOptions(flat);
        }

        quizRepository.save(quizEntity);
        return quizMapper.toResponse(quizEntity);
    }


    public void deleteQuiz(int id) {
        quizRepository.deleteById(id);
    }
}
