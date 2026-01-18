package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Verwendung: GET /api/me
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        
        // User aus Spring Security Context holen
        User user = (User) authentication.getPrincipal();
        
        // Response bauen
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("rolle", user.getRolle());
        response.put("registeredAt", user.getRegisteredAt());
        
        return ResponseEntity.ok(response);
    }


    /**
     * Health-Check Endpoint (öffentlich zugänglich)
     * 
     * Verwendung: GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend läuft!");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Public Test-Endpoint (kein Login nötig)
     * 
     * Verwendung: GET /api/public/test
     */
    @GetMapping("/public/test")
    public ResponseEntity<Map<String, String>> publicTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Dieser Endpoint ist öffentlich zugänglich!");
        
        return ResponseEntity.ok(response);
    }
}