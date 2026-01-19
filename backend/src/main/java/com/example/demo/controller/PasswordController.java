package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

        // Wir bauen die URL manuell zusammen, um sicherzustellen, dass das # erhalten bleibt
        // Supabase akzeptiert URLs in der Whitelist oft besser, wenn sie nicht voll-kodiert sind
        String targetUrl = "http://localhost:80/#/my-app/reset-password";

        Map<String, Object> supabasePayload = new HashMap<>();
        supabasePayload.put("email", email);

        // Wir hängen den redirect_to einfach an den Pfad an
        String endpoint = "/recover?redirect_to=" + targetUrl;

        return forwardToSupabase(endpoint, supabasePayload);
    }

    /**
     * Step 2: Passwort zurücksetzen (Reset Password)
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Token und Passwort dürfen nicht leer sein.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("password", newPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.set("apikey", supabaseAnonKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    supabaseAuthUrl + "/user",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            return ResponseEntity.status(response.getStatusCode()).body("Passwort erfolgreich geändert!");
        } catch (HttpClientErrorException e) {
            // z.B. Token abgelaufen oder ungültig
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
