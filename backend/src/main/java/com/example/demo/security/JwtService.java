package com.example.demo.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.UUID;

@Service
public class JwtService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validateToken(String token) {
        // Lokal akzeptieren wir jeden Token, der 3 Teile hat
        return token != null && token.split("\\.").length == 3;
    }

    public UUID getUserIdFromToken(String token) {
        try {
            // Ein JWT besteht aus Header.Payload.Signature
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            // Wir nehmen den Payload (Teil 2) und dekodieren ihn manuell
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(decodedBytes);

            // Die User-ID steht bei Supabase im Feld "sub"
            String sub = payload.get("sub").asText();
            System.out.println("DEBUG: Extrahierte UUID aus Payload: " + sub);
            return UUID.fromString(sub);
        } catch (Exception e) {
            System.err.println("Manueller Extraktionsfehler: " + e.getMessage());
            return null;
        }
    }

    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}