package com.CRUD.firstApp.auth;


import com.CRUD.firstApp.admin.Admin;
import com.CRUD.firstApp.admin.AdminRepository;
import com.CRUD.firstApp.admin.AdminRequest;
import com.CRUD.firstApp.config.JwtService;
import com.CRUD.firstApp.instructors.Instructors;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import com.CRUD.firstApp.instructors.InstructorsRequest;
import com.CRUD.firstApp.instructors.InstructorsService;
import com.CRUD.firstApp.notification.email.EmailService;


import com.CRUD.firstApp.student.Student;
import com.CRUD.firstApp.student.StudentRepository;
import com.CRUD.firstApp.student.StudentRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final InstructorsRepository InstructorsRepository;
    private final StudentRepository StudentsRepository;
    private final AdminRepository AdminRepository;

    public AuthentificationResponse registerInstructor(InstructorsRequest req) {
        Instructors instructor = new Instructors();
        instructor.setFirstName(req.firstName());
        instructor.setLastName(req.lastName());
        instructor.setEmail(req.email());
        instructor.setPassword(passwordEncoder.encode(req.password()));
        instructor.setProfileImage(req.profileImage());
        instructor.setBio(req.bio());
        instructor.setRole(Role.INSTRUCTOR);

        InstructorsRepository.save(instructor);

        emailService.sendRegistrationConfirmationEmail(
                instructor.getEmail(),
                instructor.getFirstName(),
                instructor.getLastName()
        );

        // Génère le token à partir du UserDetails (ici Instructor)
        String token = jwtService.generateToken(instructor);
        return AuthentificationResponse.builder()
                .token(token)
                .build();
    }

    public AuthentificationResponse authenticate(AuthenticationRequest request) {
        // 1) Authentifie et récupère le principal
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2) Récupère le UserDetails (ton Instructor)
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 3) Génère le token à partir de ce UserDetails
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthentificationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthentificationResponse registerStudent(StudentRequest request) {
        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setAge(request.age());
        student.setGender(request.gender());
        student.setEmail(request.email());
        student.setPhone(request.phone());
        student.setAddress(request.address()); // make sure Address object is set properly
        student.setPassword(passwordEncoder.encode(request.password()));
        student.setRole(Role.STUDENT);
        StudentsRepository.save(student);

        String token = jwtService.generateToken(student);
        return AuthentificationResponse.builder()
                .token(token)
                .build();
    }


    public AuthentificationResponse registerAdmin(AdminRequest request) {
        Admin admin = new Admin();
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        AdminRepository.save(admin);

        String token = jwtService.generateToken(admin);
        return AuthentificationResponse.builder()
                .token(token)
                .build();
    }



}
