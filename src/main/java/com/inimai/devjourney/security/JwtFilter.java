package com.inimai.devjourney.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.inimai.devjourney.repository.UserRepository;
import com.inimai.devjourney.entity.User;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
               user,
               null,
               List.of(new SimpleGrantedAuthority("ROLE_USER"))
           );
           //FOR ME TOP UNDERSTAND THE FLOW FOR FTURE USE :))
           //When a user logs in,
           //  the backend verifies the email and password using the database.
           //  If they are correct, it generates a JWT and sends it to the frontend.
           //  The frontend stores this token (for example, in localStorage)
           //  and automatically includes it in the `Authorization` 
           // header of every protected HTTP request. When a request reaches the backend, 
           // the `JwtFilter` extracts the JWT from the HTTP header and uses `JwtUtil` to
           //  validate it and extract the user's email. Using that email, it retrieves the
           //  corresponding `User` object from the database to confirm the user exists.
           //  At this point, the `User` object exists only inside the `JwtFilter`, 
           // so the controller and service cannot access it because it is just a local 
           // variable. To solve this, the filter creates a 
           // `UsernamePasswordAuthenticationToken`, which represents the authenticated user
           // , and stores it in Spring Security's `SecurityContext`. 
           // The `SecurityContext` acts as temporary storage for the current HTTP request,
           //  allowing any controller or service handling that request to access the authenticated user without needing
           //  to pass the user object through every method.

           SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}