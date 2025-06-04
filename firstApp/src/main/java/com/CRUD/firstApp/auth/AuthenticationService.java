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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    public AuthentificationResponse registerInstructor(InstructorsRequest req) throws IOException {
        // 1) Gestion de l’upload du fichier
        MultipartFile imageFile = req.profileImage();
        if (imageFile == null || imageFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile image is required");
        }
        String originalName = imageFile.getOriginalFilename();
        String storedName   = UUID.randomUUID() + "_" + originalName;
        Path target         = Paths.get("uploads/images", storedName);
        Files.createDirectories(target.getParent());
        Files.write(target, imageFile.getBytes());

        // 2) Vérification de l'unicité de l'email
        if (InstructorsRepository.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        // 3) Création de l’entité Instructors et encodage du mot de passe
        Instructors instructor = new Instructors();
        instructor.setFirstName(req.firstName());
        instructor.setLastName(req.lastName());
        instructor.setEmail(req.email());
        instructor.setPassword(passwordEncoder.encode(req.password()));
        instructor.setProfileImage(storedName);  // stocke le nom/chemin
        instructor.setBio(req.bio());
        instructor.setRole(Role.INSTRUCTOR);

        // 4) Persistance
        Instructors savedInstructor = InstructorsRepository.save(instructor);

        // 5) Envoi d’email de confirmation
        emailService.sendRegistrationConfirmationEmail(
                savedInstructor.getEmail(),
                savedInstructor.getFirstName(),
                savedInstructor.getLastName()
        );

        // 6) Génération du token JWT
        String token = jwtService.generateToken(savedInstructor);

        // 7) Réponse : on renvoie maintenant le token, l’ID et le rôle
        return AuthentificationResponse.builder()
                .token(token)
                .user_id(savedInstructor.getId())     // ID généré par la BDD
                .role(savedInstructor.getRole())      // Role.INSTRUCTOR
                .build();
    }


    public AuthentificationResponse authenticate(AuthenticationRequest request) {
        // 1) Authentifie avec Spring Security
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2) Récupère le principal (UserDetails)
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 3) Génère le JWT
        String jwtToken = jwtService.generateToken(userDetails);

        // 4) Détermine l’entité (Admin ou Instructor ou Student) pour extraire son ID et rôle
        String email = userDetails.getUsername();
        int userId;
        Role role;

        // 4a) Si c’est un Admin (trouvé par email), retourne l’ID et le rôle
        Optional<Admin> adminOpt = AdminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            userId = adminOpt.get().getId();
            role = Role.ADMIN;
        } else {
            // 4b) Sinon, si c’est un Instructor
            Optional<Instructors> instrOpt = InstructorsRepository.findByEmail(email);
            if (instrOpt.isPresent()) {
                userId = instrOpt.get().getId();
                role = Role.INSTRUCTOR;
            } else {
                // 4c) Sinon, c’est un Student
                Student student = StudentsRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé pour l’email : " + email));
                userId = student.getId();
                role = Role.STUDENT;
            }
        }

        // 5) Construit la réponse en ajoutant le token, l’ID et le rôle
        return AuthentificationResponse.builder()
                .token(jwtToken)
                .user_id(userId)
                .role(role)
                .build();
    }



    // --- 1) Enregistrement d’un Student ---
    @Transactional
    public AuthentificationResponse registerStudent(StudentRequest request) {
        // 1.1) Création et sauvegarde de l’étudiant
        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setAge(request.age());
        student.setGender(request.gender());
        student.setEmail(request.email());
        student.setPhone(request.phone());
        student.setAddress(request.address()); // veillez à bien construire l’objet Address si nécessaire
        student.setPassword(passwordEncoder.encode(request.password()));
        student.setRole(Role.STUDENT);

        Student savedStudent = StudentsRepository.save(student);

        // 1.2) Génération du JWT à partir de l’entité Student (qui implémente UserDetails)
        String token = jwtService.generateToken(savedStudent);

        // 1.3) Construction de la réponse en incluant token, user_id et role
        return AuthentificationResponse.builder()
                .token(token)
                .user_id(savedStudent.getId())   // on récupère l’ID généré par la BDD
                .role(savedStudent.getRole())    // sera Role.STUDENT
                .build();
    }


    // --- 2) Enregistrement d’un Admin ---
    @Transactional
    public AuthentificationResponse registerAdmin(AdminRequest request) {
        // 2.1) Création et sauvegarde de l’admin
        Admin admin = new Admin();
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);

        Admin savedAdmin = AdminRepository.save(admin);

        // 2.2) Génération du JWT à partir de l’entité Admin (qui implémente UserDetails)
        String token = jwtService.generateToken(savedAdmin);

        // 2.3) Construction de la réponse en incluant token, user_id et role
        return AuthentificationResponse.builder()
                .token(token)
                .user_id(savedAdmin.getId())   // on récupère l’ID généré par la BDD
                .role(savedAdmin.getRole())    // sera Role.ADMIN
                .build();
    }


}
