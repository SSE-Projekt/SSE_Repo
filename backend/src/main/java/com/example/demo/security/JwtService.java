package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    /**
     * Validiert einen Supabase JWT-Token
     * 
     * @param token Der JWT-Token aus dem Authorization Header
     * @return true wenn gültig, false wenn ungültig
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (Exception e) {
            // Token ist ungültig (abgelaufen, falsch signiert, etc.)
            System.err.println("JWT Validierung fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrahiert die User-ID aus dem Token
     * 
     * @param token Der JWT-Token
     * @return UUID des Users
     */
    public UUID getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            // Supabase speichert die User-ID im "sub" Claim
            String userId = claims.getSubject();
            
            return UUID.fromString(userId);
        } catch (Exception e) {
            throw new RuntimeException("Konnte User-ID nicht aus Token extrahieren: " + e.getMessage());
        }
    }

    /**
     * Extrahiert die E-Mail aus dem Token
     * 
     * @param token Der JWT-Token
     * @return E-Mail des Users
     */
    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            // Supabase speichert die E-Mail im "email" Claim
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Konnte E-Mail nicht aus Token extrahieren: " + e.getMessage());
        }
    }

    /**
     * Prüft ob der Token abgelaufen ist
     * 
     * @param token Der JWT-Token
     * @return true wenn abgelaufen
     */
    public boolean isTokenExpired(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // Bei Fehler als abgelaufen behandeln
        }
    }

    /**
     * Entfernt "Bearer " Prefix vom Token falls vorhanden
     * 
     * @param bearerToken Token mit oder ohne Bearer Prefix
     * @return Reiner Token
     */
    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}