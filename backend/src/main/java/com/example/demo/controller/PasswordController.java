package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordController {

    @Value("${supabase.auth.url}")
    private String supabaseAuthUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Step 1: Passwort-Reset anfordern (Forgot Password)
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email darf nicht leer sein.");
        }

        Map<String, Object> supabasePayload = new HashMap<>();
        supabasePayload.put("email", email);

        // Backend empfängt den Token direkt
        String targetUrl = "http://localhost:8080/api/auth/reset-password";
        String encodedUrl = URLEncoder.encode(targetUrl, StandardCharsets.UTF_8);

        return forwardToSupabase("/recover?redirect_to=" + encodedUrl, supabasePayload);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordToken(@RequestParam("access_token") String token) {
        // Token kommt direkt hier an
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Kein Token übergeben.");
        }

        // Du kannst den Token jetzt verwenden, z.B.:
        // - Passwort-Reset-Form ausliefern
        // - Oder direkt ein neues Passwort setzen via Supabase Admin-API

        return ResponseEntity.ok("Token empfangen: " + token);
    }

    /**
     * Step 2: Passwort zurücksetzen (Reset Password)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("access_token") String token,
                                                @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Token und Passwort dürfen nicht leer sein.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.set("apikey", supabaseAnonKey);

        Map<String, Object> body = new HashMap<>();
        body.put("password", newPassword);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    supabaseAuthUrl + "/user",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            return ResponseEntity.ok("Passwort erfolgreich geändert!");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body("Fehler beim Zurücksetzen des Passworts: " + e.getResponseBodyAsString());
        }
    }

    /**
     * Helper: POST an Supabase weiterleiten
     */
    private ResponseEntity<String> forwardToSupabase(String endpoint, Map<String, Object> payload) {
        String url = supabaseAuthUrl + endpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseAnonKey);
        headers.set("Authorization", "Bearer " + supabaseAnonKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            // z.B. Email nicht existiert oder Supabase Fehler
            // Wir geben generisch zurück, damit Frontend keine Existenz info bekommt
            return ResponseEntity.status(HttpStatus.OK).body("Wenn die E-Mail registriert ist, wurde ein Link gesendet.");
        }
    }
}
