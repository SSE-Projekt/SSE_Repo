package com.example.demo.controller;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LogController {

    @Value("${supabase.auth.url}")
    private String supabaseAuthUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    /**
     * Registriert einen neuen User in Supabase Auth
     */
    // Nutze Constructor Injection für alle Abhängigkeiten
    @Autowired
    public LogController(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @GetMapping("/testmail")
    public String testMail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("nguenaclintbryan@gmail.com");
            message.setSubject("Test");
            message.setText("Hallo!");
            mailSender.send(message);
            return "Mail wurde gesendet!";
        } catch (Exception e) {
            return "Fehler: " + e.getMessage();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String password = (String) payload.get("password");

        // 1. Die ersten 3 Buchstaben der Email extrahieren (lowercase)
        String prefix = email.substring(0, Math.min(email.length(), 3)).toLowerCase();

        // 2. In der DB zählen, wie viele es schon gibt
        long count = userRepository.countByNameStartingWith(prefix);

        // 3. Username generieren (z.B. cli1, cli2...)
        String generatedUsername = prefix + (count + 1);

        // 4. Payload für Supabase vorbereiten
        Map<String, Object> supabasePayload = new HashMap<>();
        supabasePayload.put("email", email);
        supabasePayload.put("password", password);

        // Metadata vorbereiten
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("user_name", generatedUsername);

        // Falls das Frontend noch weitere Metadaten mitschickt, diese hinzufügen
        if (payload.containsKey("metadata")) {
            Map<String, Object> incomingMetadata = (Map<String, Object>) payload.get("metadata");
            metadata.putAll(incomingMetadata);
        }

        supabasePayload.put("data", metadata);

        ResponseEntity<String> response = forwardToSupabase("/signup", supabasePayload);

        // 2. Wenn Supabase Erfolg meldet (200 oder 201), senden wir die E-Mail
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                // Log zur Kontrolle in der IDE-Konsole
                System.out.println("Versuche Willkommens-E-Mail zu senden an: " + email);
                sendWelcomeEmail(email, generatedUsername);
                System.out.println("E-Mail wurde erfolgreich an den Mail-Server übergeben.");
            } catch (Exception e) {
                // WICHTIG: Fehler loggen, aber die Antwort (Success) trotzdem zurückgeben
                System.err.println("Fehler beim E-Mail-Versand: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * sendWelcome
     */
    private void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@secure-notes.de");
        message.setTo(to);
        message.setSubject("Willkommen bei Secure Notes!");
        message.setText("Hallo, " + "\n\nvielen Dank für deine Registrierung!\n" +
                "Dein automatisch generierter Benutzername lautet: " + username + "\n\n" +
                "Viel Spaß mit der Anwendung!");

        mailSender.send(message);
    }

    /**
     * Loggt einen User ein und gibt das JWT zurück
     */
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody Map<String, Object> payload) {
        String loginIdentifier = (String) payload.get("login"); // Kann Email ODER Username sein
        String password = (String) payload.get("password");

        String emailToUse = loginIdentifier;

        // 1. Prüfen, ob der Login-Identifier eine Email ist.
        // Wenn nicht, suchen wir den Usernamen in der DB.
        if (!loginIdentifier.contains("@")) {
            // Wir suchen in deiner UserRepository nach dem Usernamen
            // Ich nehme an, das Feld in deiner DB heißt 'name' (wie im Repository-Check)
            var userOptional = userRepository.findByName(loginIdentifier);

            if (userOptional.isPresent()) {
                emailToUse = userOptional.get().getEmail();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Benutzername nicht gefunden.");
            }
        }

        // 2. Payload für Supabase vorbereiten (Email + Passwort)
        Map<String, Object> supabasePayload = new HashMap<>();
        supabasePayload.put("email", emailToUse);
        supabasePayload.put("password", password);

        // 3. An Supabase senden
        String endpoint = "/token?grant_type=password";
        return forwardToSupabase(endpoint, supabasePayload);
    }

    private ResponseEntity<String> forwardToSupabase(String endpoint, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseAnonKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForEntity(supabaseAuthUrl + endpoint, entity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}