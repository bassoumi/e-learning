package com.CRUD.firstApp.student;


import com.CRUD.firstApp.instructors.Instructors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setPhone(request.phone());
        student.setAddress(request.address());
        student.setGender(request.gender());
        student.setAge(request.age());
        return student;
    }
    public StudentResponse toResponse(Student student) {
        // 1) On copie d’abord tous les instructeurs dans une ArrayList indépendante
        List<Instructors> copyOfInstructors = new ArrayList<>(student.getInstructors());

        // 2) On peut maintenant itérer sur copyOfInstructors sans risque de ConcurrentModification
        List<Integer> instructorIds = copyOfInstructors.stream()
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




}
