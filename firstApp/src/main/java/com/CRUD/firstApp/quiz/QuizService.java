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

    public QuizResponse updateQuiz(int id , QuizRequest request) {
        var QuizToEntity = quizRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Quiz not found with id " + id));
        if (StringUtils.hasText(request.title())){
            QuizToEntity.setTitle(request.title());
        }
        if (StringUtils.hasText(request.questions())){
            QuizToEntity.setQuestions(request.questions());
        }
        if (StringUtils.hasText(request.answers())){
            QuizToEntity.setAnswers(request.answers());
        }
        if (StringUtils.hasText(request.options())){
            QuizToEntity.setOptions(request.options());
        }

        quizRepository.save(QuizToEntity);
        return quizMapper.toResponse(QuizToEntity);

    }


    public void deleteQuiz(int id) {
        quizRepository.deleteById(id);
    }
}
