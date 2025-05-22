package com.CRUD.firstApp.instructors;


import org.springframework.stereotype.Component;

@Component
public class InstructorsMapper {

    public Instructors toIntructors(InstructorsRequest request) {
        Instructors instructors = new Instructors();
        instructors.setFirstName(request.firstName());
        instructors.setLastName(request.lastName());
        instructors.setEmail(request.email());
        instructors.setProfileImage(request.profileImage());
        instructors.setBio(request.bio());
        return instructors;
    }

    public InstructorsResponce toResponce(Instructors instructors) {
        return new InstructorsResponce(
                instructors.getFirstName(),
                instructors.getLastName(),
                instructors.getEmail(),
                instructors.getProfileImage(),
                instructors.getBio()
        );
    }


}
