package com.CRUD.firstApp.student;


import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    public final StudentMapper studentMapper;
    public final StudentRepository studentRepository;

    public StudentService(StudentMapper studentMapper, StudentRepository studentRepository) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
    }

 public List<Student> getAllStudents() {
        return studentRepository.findAll();
 }
}
