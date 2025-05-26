package com.CRUD.firstApp.config;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;



//filter the method take request , response , filterchain(pass to other filter with the same request that we have )
//in our code we have to filter in filterChain (First filter (jwtAuthFilter):This filter checks the JWT token in the request header. Second filter (UsernamePasswordAuthenticationFilter):Normally, this filter handles the login process where username and password are submitted (e.g., via a login form).
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        System.out.println(">>> Using UserDetailsService: " + userDetailsService.getClass());
    }


    @Override
    protected void doFilterInternal(
          @NotNull HttpServletRequest request,
          @NotNull    HttpServletResponse response,
          @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String UserEmail;
        // if we have the token we continue if we donnt have the token it  will return to the securityConfiguration to search if this request need authetification or not
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
// we have the token so we sart by removing the first 7 letters from the substring
        jwt = authHeader.substring(7);
        //call the jwtservice and extract the username from the claims body from the token (have token params)
        UserEmail = jwtService.extractUserName(jwt);
        //chek if the username is not empty aand it is the first time that the user connect (authentification is empty)
        if (UserEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//check if the user exsist by his username in our database and if it is he will return the role , password hashshed
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(UserEmail);
            //check using our service if the jwt is valid or not expired or not , match info or not , valid signature "zzzz" or not
            if (jwtService.isTokenValid(jwt, userDetails)) {
                //create a spring authentificaition to make the user authentificated
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                //set details to authentication
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //set authentification so the user is now autehtificated
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        //continu to run the rest defualt filter after the two that we add
        filterChain.doFilter(request, response);

    }
}
