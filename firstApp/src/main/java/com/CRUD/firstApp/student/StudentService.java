package com.CRUD.firstApp.student;


import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import com.CRUD.firstApp.notification.Notification;
import com.CRUD.firstApp.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final  StudentMapper studentMapper;
    private final InstructorsRepository instructorRepository;
    private final NotificationService notificationService ;


    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper, InstructorsRepository instructorRepository, NotificationService notificationService) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.instructorRepository = instructorRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse createStudent(StudentRequest studentRequest) {
        var studentEntity = studentMapper.toEntity(studentRequest);
        studentRepository.save(studentEntity);
        return studentMapper.toResponse(studentEntity);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(int id) {
        Student student = studentRepository.findByIdWithInstructors(id)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant introuvable avec l’ID " + id));

        // Vu qu'on a déjà fait JOIN FETCH, la collection instructors est déjà remplie
        // On peut donc streamer directement sans copie si on veut :
        List<Integer> instructorIds = student.getInstructors().stream()
                .map(Instructors::getId)
                .collect(Collectors.toList());

        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getEmail(),
                instructorIds
        );
    }

    public List<StudentResponse> getStudentsByName(String name) {
        var students = studentRepository.findByFirstName(name);
        if(students.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO STUDENTS FOUND WITH THIS NAME : " +name);
        }
              return  students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }


    public StudentResponse updateStudent(int id, @Valid StudentRequest request) {
        Student existingStudent = studentRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STUDENT NOT FOUND"));


        if (request.firstName() != null) {
            String fn = request.firstName().trim();
            if (fn.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le prénom ne peut pas être vide"
                );
            }
            existingStudent.setFirstName(fn);
        }

        if (request.lastName() != null) {
            String ln = request.lastName().trim();
            if (ln.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le nom ne peut pas être vide"
                );
            }
            existingStudent.setLastName(ln);
        }

        if (request.email() != null) {
            String mail = request.email().trim();
            if (mail.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "L’email ne peut pas être vide"
                );
            }
            // Validation basique du format d’email
            if (!mail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le format de l’email est invalide"
                );
            }
            existingStudent.setEmail(mail);
        }

        if (request.phone() != null) {
            String phone = request.phone().trim();
            if (phone.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le téléphone ne peut pas être vide"
                );
            }
            existingStudent.setPhone(phone);
        }

        if (request.address() != null) {
            existingStudent.setAddress(request.address());
        }

        if (request.gender() != null) {
            String gen = request.gender().trim();
            if (gen.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le genre ne peut pas être vide"
                );
            }
            existingStudent.setGender(gen);
        }


            Integer a = request.age();

            existingStudent.setAge(a);


        // 3) Sauvegarde dans la base de données
        Student saved = studentRepository.save(existingStudent);

        // 4) Retourner la réponse mappée
        return studentMapper.toResponse(saved);


    }

    public void deleteStudent(int id) {
        studentRepository.deleteById(id);

    }

    public StudentResponse getStudentsByEmail(@NotBlank String email) {
        return studentMapper.toResponse(studentRepository.findByEmail(email).get());

    }

    //subscribe method :


    @Transactional(readOnly = true)
    public StudentResponse getSubscription(int studentId) {
        System.out.println("→ getSubscription appelé pour studentId=" + studentId);

        // 1) Récupérer l’étudiant
        Student student = studentRepository.findByIdWithInstructors(studentId)
                .orElseThrow(() -> {
                    System.out.println("!! Étudiant introuvable (ID: " + studentId + ")");
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Étudiant introuvable (ID: " + studentId + ")"
                    );
                });
        System.out.println("→ Étudiant récupéré : ID=" + student.getId() + ", firstName=" + student.getFirstName());

        // 2) Extraire les IDs des instructors
        List<Integer> instructorIds = student.getInstructors().stream()
                .map(Instructors::getId)
                .collect(Collectors.toList());
        System.out.println("→ Liste des instructorIds = " + instructorIds);

        // 3) Construire et renvoyer le DTO
        StudentResponse dto = new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getEmail(),
                instructorIds
        );
        System.out.println("→ DTO créé : " + dto);

        return dto;
    }


    @Transactional
    public StudentResponse subscribeToInstructor(int studentId, int instructorId) {
        System.out.println("Début de subscribeToInstructor : studentId=" + studentId + ", instructorId=" + instructorId);

        // 1) On récupère le student AVEC tous ses instructors pré-chargés
        Student student = studentRepository.findByIdWithInstructors(studentId)
                .orElseThrow(() -> {
                    System.out.println("Étudiant introuvable (ID: " + studentId + ")");
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Étudiant introuvable (ID: " + studentId + ")"
                    );
                });
        System.out.println("Student récupéré : ID=" + student.getId());

        // 2) On récupère l’instructor existant
        Instructors instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> {
                    System.out.println("Instructeur introuvable (ID: " + instructorId + ")");
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Instructeur introuvable (ID: " + instructorId + ")"
                    );
                });
        System.out.println("Instructor récupéré : ID=" + instructor.getId());

        // 3) On modifie directement la collection pré-chargée
        System.out.println("Avant ajout, nombre d'instructors = " + student.getInstructors().size());
        student.getInstructors().add(instructor);
        System.out.println("Après ajout, nombre d'instructors = " + student.getInstructors().size());

        // 4) Sauvegarde du Student (la relation many-to-many sera mise à jour)
        System.out.println("Avant appel à studentRepository.save");
        studentRepository.save(student);
        System.out.println("Après appel à studentRepository.save");

        // 5) On reconstruit le DTO, sachant que student.getInstructors() est déjà initialisée
        StudentResponse response = studentMapper.toResponse(student);
        System.out.println("Fin de subscribeToInstructor, DTO généré");

        return response;
    }




    @Transactional
    public StudentResponse updateSubscription(int studentId, List<Integer> newInstructorIds) {
        // 1) Récupérer le student AVEC ses instructors préchargés
        Student student = studentRepository.findByIdWithInstructors(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable (ID: " + studentId + ")"
                ));

        // 2) Construire le nouvel ensemble d’instructeurs
        Set<Instructors> newInstructors = new HashSet<>();
        for (int id : newInstructorIds) {
            Instructors instructor = instructorRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Instructeur introuvable (ID: " + id + ")"
                    ));
            newInstructors.add(instructor);
        }

        // 3) Remplacer entièrement la collection
        student.setInstructors(newInstructors);

        // 4) Persister la mise à jour
        studentRepository.save(student);

        // 5) Retourner le DTO (student.getInstructors() est déjà initialisée)
        return studentMapper.toResponse(student);
    }



    @Transactional
    public void unsubscribeFromInstructor(int studentId, int instructorId) {
        // 1) Charger l’étudiant AVEC sa collection d’instructeurs
        Student student = studentRepository
                .findByIdWithInstructors(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Étudiant introuvable (ID: " + studentId + ")"
                ));

        // 2) Charger l’instructeur à retirer
        Instructors instructor = instructorRepository
                .findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instructeur introuvable (ID: " + instructorId + ")"
                ));

        // 3) Retirer l’instructeur de la collection gérée (PersistentSet)
        boolean removed = student.getInstructors().remove(instructor);
        if (!removed) {
            // Si l’instructeur n’était pas déjà dans la liste, on peut
            // décider de renvoyer un 404 ou simplement ignorer. Ici, on ignore.
        }

        // 4) Hibernate va gérer le flush automatiquement (DELETE dans student_instructor_subscription)
        //    au commit de la transaction. Pas besoin de studentRepository.save(student) explicite.
    }



    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(int studentId) {
        return notificationService.getUnreadNotifications(studentId);
    }


    @Transactional
    public void markNotificationAsRead(int studentId, Integer notificationId) {
        // Vous pouvez vérifier que la notification appartient bien à cet étudiant
        // (sinon lever une exception 403)
        Notification n = notificationService.getById(notificationId);

        if (n.getStudent().getId() != studentId) {
            throw new RuntimeException("Cette notification n'appartient pas à l'étudiant " + studentId);
        }

        notificationService.markAsRead(notificationId);
    }





}