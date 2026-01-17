package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users") // Alle Endpoints fangen mit /api/users an
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Gibt eine Liste aller registrierten Benutzer zurück.
     * SSE-PRINZIP: Nur notwendige Daten (Name, Email) werden exponiert.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, String>> response = new ArrayList<>();

        for (User user : users) {
            Map<String, String> userMap = new HashMap<>();
            // Wir senden NICHT die ID oder das Passwort!
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            response.add(userMap);
        }

        return ResponseEntity.ok(response);
    }
}