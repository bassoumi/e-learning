package com.CRUD.firstApp.student;


import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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


    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper, InstructorsRepository instructorRepository) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.instructorRepository = instructorRepository;
    }

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

    public StudentResponse getStudentById(int id) {
        return studentMapper.toResponse(studentRepository.findById(id).get());
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


    public StudentResponse getSubscription(int studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Étudiant introuvable (ID: " + studentId + ")"));

        return studentMapper.toResponse(student);
    }




    public StudentResponse subscribeToInstructor(int studentId, int instructorId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Étudiant introuvable (ID: " + studentId + ")"));

        var instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Instructeur introuvable (ID: " + instructorId + ")"));

        // Ajouter l’instructeur à la liste
        student.getInstructors().add(instructor);
        studentRepository.save(student);

        return studentMapper.toResponse(student);
    }



    public StudentResponse updateSubscription(int studentId, List<Integer> newInstructorIds) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Étudiant introuvable (ID: " + studentId + ")"));

        Set<Instructors> newInstructors = new HashSet<>();
        for (int id : newInstructorIds) {
            var instructor = instructorRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Instructeur introuvable (ID: " + id + ")"));
            newInstructors.add(instructor);
        }

        student.setInstructors(newInstructors);
        studentRepository.save(student);

        return studentMapper.toResponse(student);
    }









}