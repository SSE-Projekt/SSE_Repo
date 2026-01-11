package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getPublicKey())  // ⭐ GEÄNDERT
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (Exception e) {
            System.err.println("JWT Validierung fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getPublicKey())  // ⭐ GEÄNDERT
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            String userId = claims.getSubject();
            return UUID.fromString(userId);
        } catch (Exception e) {
            throw new RuntimeException("Konnte User-ID nicht aus Token extrahieren: " + e.getMessage());
        }
    }

    // ⭐ NEU: Public Key für ES256
    private PublicKey getPublicKey() throws Exception {
        // Für ES256: JWT Secret ist eigentlich ein Public Key
        // Entferne "-----BEGIN PUBLIC KEY-----" und "-----END PUBLIC KEY-----"
        String publicKeyPEM = jwtSecret
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePublic(spec);
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Konnte E-Mail nicht aus Token extrahieren: " + e.getMessage());
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}