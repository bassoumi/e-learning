package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentResponce;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.quiz.Quiz;
import com.CRUD.firstApp.quiz.QuizRequest;
import com.CRUD.firstApp.quiz.QuizResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public Courses toEntityCourses(CoursRequest request ,  Categorie category,
                                   List<Instructors> instructors){


        Courses courses = new Courses();
        courses.setTitle(request.title());
        courses.setDescription(request.description());
        courses.setCoverImage(request.coverImage());
        courses.setLanguage(request.language());
        courses.setLevel(request.level());
        courses.setShortDescription(request.shortDescription());

        if (request.metadata() != null) {
            CourseMetaDataRequest metaReq = request.metadata();

            CourseMetaData meta = new CourseMetaData();
            meta.setDuration(metaReq.duration());
            meta.setTags(metaReq.tags());
            meta.setObjectives(metaReq.objectives());

            LocalDateTime now = LocalDateTime.now();
            meta.setCreatedAt(now);
            meta.setUpdatedAt(now);

            courses.setMetadata(meta);
        }

        // ✅ Mapper les contenus ici
        if (request.contents() != null && !request.contents().isEmpty()) {
            List<Content> contents = request.contents().stream()
                    .map(contentReq -> {
                        Content content = new Content();
                        content.setTitle(contentReq.title());
                        content.setDescription(contentReq.description());
                        content.setVideoUrl(contentReq.videoUrl());
                        content.setOrderContent(contentReq.orderContent());
                        content.setCourse(courses); // Lien vers le cours
                        return content;
                    })
                    .collect(Collectors.toList());

            courses.setContents(contents);
        }

        if (request.quiz() != null) {
            QuizRequest quizReq = request.quiz();
            Quiz quiz = new Quiz();
            quiz.setTitle(quizReq.title());
            quiz.setQuestions(quizReq.questions());
            if (quizReq.options() != null && !quizReq.options().isEmpty()) {
                List<String> flatOptions = quizReq.options().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                quiz.setOptions(flatOptions);
            }            quiz.setAnswers(quizReq.answers());
            quiz.setCourse(courses); // Liaison bidirectionnelle
            courses.setQuiz(quiz);
        }


        // ManyToOne category
        courses.setCategorie(category);

        // ManyToMany instructors
        courses.setInstructors(instructors);

        return courses;
    }


    public CourseResponse toResponceCourses(Courses course) {
        String categoryName = course.getCategorie().getNom();

        List<String> instructorNames = course.getInstructors().stream()
                .map(Instructors::getFirstName)
                .collect(Collectors.toList());

        List<ContentResponce> contents = course.getContents().stream()
                .map(c -> new ContentResponce(
                        c.getTitle(),
                        c.getDescription(),
                        c.getVideoUrl(),
                        c.getOrderContent()
                ))
                .collect(Collectors.toList());

        QuizResponse quizResponse = null;
        if (course.getQuiz() != null) {
            quizResponse = new QuizResponse(
                    course.getQuiz().getTitle(),
                    course.getQuiz().getQuestions(),
                    course.getQuiz().getOptions(),
                    course.getQuiz().getAnswers()
            );
        }

        return new CourseResponse(
                course.getTitle(),
                course.getDescription(),
                course.getShortDescription(),
                course.getLevel(),
                course.getLanguage(),
                course.getCoverImage(),
                categoryName,
                course.getMetadata(),
                instructorNames,
                contents,
                quizResponse // Ajout ici
        );
    }



}



