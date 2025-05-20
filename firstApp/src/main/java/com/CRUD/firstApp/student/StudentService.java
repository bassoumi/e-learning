package com.CRUD.firstApp.student;


import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final  StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper ) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public StudentResponse createStudent(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }



}