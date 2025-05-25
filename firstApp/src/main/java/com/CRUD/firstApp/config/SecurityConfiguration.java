package com.CRUD.firstApp.config;


import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


// bean used when the request come to out backend part
    //desable crrf attack
    //no permission or authentication required for .requestMatchers("/your-public-endpoint").permitAll()
    //permission or authentication required for  .anyRequest().authenticated()
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                //if the requet return from the jwtauthfilter , we dont have token we search in the  permitAll
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll() // ⬅️ replace with your real public path (e.g., /api/auth/**)
                        // if the requet return from the jwtauthfilter , we have token we search in the  authenticated() route protect with token
                        .anyRequest().authenticated()
                )
                //With SessionCreationPolicy.STATELESS, every request must send its authentication info (like the JWT token) again, because the server doesn't remember anything from before
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //if we have autehntification request (/login) in the body so we go here
                .authenticationProvider(authenticationProvider)

                //addFilterBefore use the jwtAuthFilter to valid the token and chek the authentification before the UsernamePasswordAuthenticationFilter so it cant have eurreur
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
