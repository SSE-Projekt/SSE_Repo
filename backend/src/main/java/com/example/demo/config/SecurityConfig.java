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
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        
        // NEU: Explizite Fehlerbehandlung hinzufügen
        // Wenn Spring Security den Zugriff verweigert, gibt es standardmäßig 403.
        // Mit diesem EntryPoint können wir sicherstellen, dass wir 401 statt 403 bekommen,
        // wenn der Token einfach nur fehlt oder ungültig ist.
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Nicht autorisiert: " + authException.getMessage());
            })
        )

        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth
            // WICHTIG: Erlaube den Zugriff auf /error, damit Fehlermeldungen vom Backend 
            // nicht durch eine 403-Security-Sperre ersetzt werden.
            .requestMatchers("/error").permitAll()
            
            .requestMatchers("/api/public/**", "/api/auth/**", "/api/notes/**").permitAll()
            .requestMatchers("/api/health").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/notes/public").permitAll()

            .anyRequest().authenticated()
        )

        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Erlaube Frontend-URL (Vue.js läuft auf Port 5173)
        configuration.setAllowedOrigins(Arrays.asList(
          "http://localhost:5173",  // Lokale Entwicklung (Vite Standard-Port)
                "http://localhost:3000", // Lokale Entwicklung (Standard-Port 80)
                "http://localhost"       // Docker-Umgebung (Standard-Port 80)
        ));
           

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
