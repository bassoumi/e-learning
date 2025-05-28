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
            List<Instructors> instructors,
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

            // map each QuestionRequest → Question embeddable
            if (quizReq.questions() != null && !quizReq.questions().isEmpty()) {
                List<QuizQuestions> questionEntities = quizReq.questions().stream()
                        .map(qr -> {
                            QuizQuestions q = new QuizQuestions();
                            q.setText(qr.text());
                            q.setOptions(new ArrayList<>(qr.options()));
                            q.setAnswer(qr.answer());
                            return q;
                        })
                        .toList();
                quiz.setQuestions(questionEntities);
            }

            // link quiz ↔ course
            quiz.setCourse(courses);
            courses.setQuiz(quiz);
        }

        courses.setCategorie(category);
        courses.setInstructors(instructors);

        return courses;
    }

    public CourseResponse toResponceCourses(Courses course) {
        // 1) Récupérer le nom de la catégorie
        String categoryName = course.getCategorie().getNom();

        // 2) Récupérer les noms d'instructeurs (s'il y en a)
        List<String> instructorNames = (course.getInstructors() != null)
                ? course.getInstructors().stream()
                .map(Instructors::getFirstName)
                .collect(Collectors.toList())
                : Collections.emptyList();

        // 3) Mapper la liste de contents en ContentResponce (ou renvoyer une liste vide)
        List<ContentResponce> contents = (course.getContents() != null)
                ? course.getContents().stream()
                .map(c -> new ContentResponce(
                        c.getId(),
                        String.valueOf(c.getCourse().getId()), // or just c.getCourse().getId() if courseId is an int
                        c.getTitle(),
                        c.getDescription(),
                        c.getVideoUrl(),
                        c.getOrderContent()
                ))
                .collect(Collectors.toList())
                : Collections.emptyList();

        // 4) Mapper le quiz si présent
        QuizResponse quizResponse = null;
        if (course.getQuiz() != null) {
            Quiz q = course.getQuiz();

            List<QuestionResponse> questionDtos = q.getQuestions().stream()
                    .map(ques -> new QuestionResponse(
                            ques.getText(),
                            ques.getOptions(),
                            ques.getAnswer()
                    ))
                    .toList();

            quizResponse = new QuizResponse(
                    q.getId(),           // quiz id
                    q.getTitle(),        // quiz title
                    course.getId(),      // course id
                    questionDtos         // mapped questions
            );
        }


        // 5) Construire et retourner le CourseResponse
        return new CourseResponse(
                course.getId(),
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
                quizResponse
        );
    }

}



