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

        // 2. Überprüfung des Headers
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Token extrahieren
        jwt = authHeader.substring(7);

        try {
            // 4. Token-Validierung und Benutzeridentifikation
            if (jwtService.validateToken(jwt)) {
                UUID userId = jwtService.getUserIdFromToken(jwt);

                // SSE-PRINZIP: Wir suchen nur den User in der DB. 
                // Keine automatische Erstellung hier, um SQL-Konflikte (500er Fehler) zu vermeiden.
                var userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    System.out.println("DEBUG: Erfolgreicher Login für: " + user.getEmail());

                    // Authentifizierungstoken für den SecurityContext erstellen
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Authentifizierung im Kontext speichern
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Logge die Warnung, wenn der User in Auth existiert, aber nicht in unserer Tabelle
                    System.err.println("Sicherheits-Warnung: User ID " + userId + " fehlt in der lokalen Tabelle.");
                }
            }
        } catch (Exception e) {
            // SSE-Prinzip: Fail-Safe Handling. Das System stürzt nicht ab, wenn das Token ungültig ist.
            System.err.println("JWT-Validierungsfehler: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // 5. WICHTIG: Die Filterkette muss immer fortgesetzt werden
        filterChain.doFilter(request, response);
    }
}