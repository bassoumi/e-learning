package com.CRUD.firstApp.config;


import com.CRUD.firstApp.admin.Admin;
import com.CRUD.firstApp.admin.AdminRepository;
import com.CRUD.firstApp.instructors.InstructorsRepository;
import com.CRUD.firstApp.student.StudentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor

public class ApplicationConfig {
    private final InstructorsRepository instructorsRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

//    When you put @Bean on a method inside a @Configuration class, Spring will:
//
//    Run that method automatically when the application starts.
//
//    Take the object returned by the method and register it as a bean in the Spring container.
//
//            Make this bean available for dependency injection anywhere in your app.


    // It returns a UserDetails object, which contains username, hashed password, and roles.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Try Admin first (login with username)
            Optional<Admin> admin = adminRepository.findByEmail(username);
            if (admin.isPresent()) {
                return admin.get();
            }

            // Try Instructor and Student by email
            return instructorsRepository.findByEmail(username)
                    .map(user -> (UserDetails) user)
                    .or(() -> studentRepository.findByEmail(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }





    // authProvider check if is the same password and the username in userdetails
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration confi) throws Exception {
        return confi.getAuthenticationManager();
    }

//tells Spring Security how to check the password during login.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
