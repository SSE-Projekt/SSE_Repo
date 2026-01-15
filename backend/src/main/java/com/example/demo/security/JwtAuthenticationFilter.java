package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extrahiere den Authorization Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdString;

        // 2. Wenn kein Header da ist oder er nicht mit "Bearer " beginnt:
        // Einfach weiterreichen. Spring Security prüft danach in der Config,
        // ob der Pfad (z.B. /api/notes/public) auch ohne Auth erlaubt ist.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Token extrahieren
        jwt = authHeader.substring(7);

        try {
            if (jwtService.validateToken(jwt)) {
                UUID userId = jwtService.getUserIdFromToken(jwt);

                // User finden oder automatisch erstellen (Just-in-Time Provisioning)
                User user = userRepository.findById(userId).orElseGet(() -> {
                    System.out.println("DEBUG: Erstelle neuen User in DB für ID: " + userId);
                    User newUser = new User();
                    newUser.setId(userId);
                    // Falls du die Email extrahieren kannst: jwtService.getEmailFromToken(jwt)
                    newUser.setEmail("user-" + userId.toString().substring(0, 5) + "@example.com");
                    return userRepository.save(newUser);
                });

                System.out.println("DEBUG: Authentifiziere User: " + user.getEmail());

                // Token für Spring Security erstellen
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // WICHTIG: Den User im Sicherheits-Kontext speichern
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            System.err.println("DEBUG-JWT-ERROR: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // 5. WICHTIG: Den Request auf jeden Fall weitergeben
        filterChain.doFilter(request, response);
    }
}