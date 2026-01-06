package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF deaktivieren für API (JWT-basiert)
            .csrf(AbstractHttpConfigurer::disable)

            // CORS konfigurieren
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Ergänzung: Falls ein Auth-Fehler auftritt, wird dieser oft als 403 getarnt
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Nicht autorisiert");
                        })
                )
            // Session Management: Stateless (kein Session-Cookie)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Autorisierungs-Regeln
            .authorizeHttpRequests(auth -> auth
                // Öffentliche Endpoints (kein Login nötig)
                .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()

                // Alle anderen Endpoints brauchen Authentication
                .anyRequest().authenticated()
            )

            // JWT-Filter VOR dem Standard-Auth-Filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Erlaube Frontend-URL (Vue.js läuft auf Port 5173)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));

        // Erlaube alle HTTP-Methoden
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Erlaube alle Headers
        configuration.setAllowedHeaders(List.of("*"));

        // Erlaube Credentials (Cookies, Authorization Header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
