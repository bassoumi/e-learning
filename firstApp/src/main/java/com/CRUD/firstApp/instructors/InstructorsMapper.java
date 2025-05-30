package com.CRUD.firstApp.instructors;


import com.CRUD.firstApp.auth.Role;
import org.springframework.stereotype.Component;

@Component
public class InstructorsMapper {

    public Instructors toInstructors(InstructorsRequest req, String storedFileName) {
        Instructors instructor = new Instructors();
        instructor.setFirstName( req.firstName() );
        instructor.setLastName(  req.lastName()  );
        instructor.setEmail(     req.email()     );
        instructor.setPassword(  req.password()  );      // mot de passe déjà encodé
        instructor.setProfileImage(storedFileName);      // nom du fichier en base
        instructor.setBio(       req.bio()       );
        instructor.setRole(      Role.INSTRUCTOR );
        return instructor;
    }


    public InstructorsResponce toResponce(Instructors instructors) {
        return new InstructorsResponce(
                instructors.getId(),
                instructors.getFirstName(),
                instructors.getLastName(),
                instructors.getEmail(),
                instructors.getProfileImage(),
                instructors.getBio()
        );
    }


}
