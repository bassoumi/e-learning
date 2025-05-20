package com.CRUD.firstApp.student;


import org.springframework.stereotype.Component;

@Component
public class StudentMapper {


    public Student toEntity(StudentRequest studentRequest) {
        Student student = new Student();
        student.setFirstName(studentRequest.firstName());
        student.setLastName(studentRequest.lastName());
        student.setEmail(studentRequest.email());
        student.setEmail(studentRequest.email());
        student.setPhone(studentRequest.phone());
        student.setGender(studentRequest.gender());
        student.setAddress(studentRequest.address());
        student.setAge(studentRequest.age());
        return student;
    }




public StudentResponse toResponse(Student student){
    return new StudentResponse(
            student.getFirstName(),
            student.getLastName(),
            student.getGender(),
            student.getEmail()
    );

    }
}
