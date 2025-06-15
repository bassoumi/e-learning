package com.CRUD.firstApp.courses;


import com.CRUD.firstApp.Categorie.Categorie;
import com.CRUD.firstApp.contentcourse.Content;
import com.CRUD.firstApp.contentcourse.ContentResponce;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.quiz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    public Courses toEntityCourses(
            CoursRequest request,
            Categorie category,
            Instructors instructors,
            String coverImageFilename
    ) {
        Courses courses = new Courses();
        courses.setTitle(request.title());
        courses.setDescription(request.description());
        courses.setCoverImage(coverImageFilename);
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

        if (request.contents() != null && !request.contents().isEmpty()) {
            List<Content> contents = request.contents().stream()
                    .map(contentReq -> {
                        Content content = new Content();
                        content.setTitle(contentReq.title());
                        content.setDescription(contentReq.description());
                        content.setVideoUrl(contentReq.videoUrl());
                        content.setOrderContent(contentReq.orderContent());
                        content.setCourse(courses);
                        return content;
                    })
                    .collect(Collectors.toList());
            courses.setContents(contents);
        }

        if (request.quiz() != null) {
            QuizRequest quizReq = request.quiz();

            Quiz quiz = new Quiz();
            quiz.setTitle(quizReq.title());
            // — no quiz.setId(...) here! Let Hibernate generate it.

            // Map each QuestionRequest → QuizQuestions, using the helper to keep links in sync
            if (quizReq.questions() != null && !quizReq.questions().isEmpty()) {
                for (var qr : quizReq.questions()) {
                    QuizQuestions q = new QuizQuestions();
                    q.setText(qr.text());
                    q.setOptions(new ArrayList<>(qr.options()));
                    q.setAnswer(qr.answer());
                    quiz.addQuestion(q);    // sets q.setQuiz(this) AND adds to quiz.getQuestions()
                }
            }

            // Link Quiz ↔ Course
            quiz.setCourse(courses);
            courses.setQuiz(quiz);
        }

        // 4. Set category + instructors
        courses.setCategorie(category);
        courses.setInstructor(instructors);

        return courses;
    }

    public CourseResponse toResponceCourses(Courses course) {
        // 1) Vérifier que la catégorie n’est pas nulle (ou gérer son absence)
        String categoryName = (course.getCategorie() != null)
                ? course.getCategorie().getNom()
                : null;
        Integer categoryId = (course.getCategorie() != null)
                ? course.getCategorie().getId()
                : null;

        String instructorProfileImage = (course.getInstructor() != null)
                ? course.getInstructor().getProfileImage()
                : null;


        // 2) Récupérer et tester l’instructeur
        Integer instructorId = (course.getInstructor() != null)
                ? course.getInstructor().getId()
                : null;
        String instructorNames = (course.getInstructor() != null)
                ? course.getInstructor().getFirstName()
                : null;

        // 3) Mapper les contenus (inchangé)
        List<ContentResponce> contents = (course.getContents() != null)
                ? course.getContents().stream()
                .map(c -> new ContentResponce(
                        c.getId(),
                        String.valueOf(c.getCourse().getId()),
                        c.getTitle(),
                        c.getDescription(),
                        c.getVideoUrl(),
                        c.getOrderContent()
                ))
                .collect(Collectors.toList())
                : Collections.emptyList();

        // 4) Mapper le quiz (idem)
        QuizResponse quizResponse = null;
        if (course.getQuiz() != null) {
            Quiz q = course.getQuiz();
            List<QuestionResponse> questionDtos = q.getQuestions().stream()
                    .map(ques -> new QuestionResponse(
                            ques.getText(),
                            ques.getOptions(),
                            ques.getAnswer()
                    ))
                    .collect(Collectors.toList());

            quizResponse = new QuizResponse(
                    q.getId(),
                    q.getTitle(),
                    course.getId(),
                    questionDtos
            );
        }

        // 5) Construire le CourseResponse en passant des Integer pour categoryId et instructorId
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getShortDescription(),
                course.getLevel(),
                course.getLanguage(),
                course.getCoverImage(),
                categoryName,
                categoryId,
                instructorId,
                course.getMetadata(),
                instructorNames,
                contents,
                quizResponse,
                instructorProfileImage
        );
    }



}



