package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @Column(name = "id")
    private UUID id;  // Kommt von Supabase auth.users

    @Column(name = "name")
    @NotBlank(message = "Name ist erforderlich")
    private String name;

    @Column(name = "email", unique = true)
    @Email(message = "Ungültige E-Mail-Adresse")
    @NotBlank(message = "E-Mail ist erforderlich")
    private String email;

    @Column(name = "rolle")
    private Integer rolle;  // 1 = READ, 2 = WRITE

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    // Constructor
    public User() {
        this.registeredAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRolle() {
        return rolle;
    }

    public void setRolle(Integer rolle) {
        this.rolle = rolle;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    // Helper-Methoden für Rollen
    public boolean isReadRole() {
        return rolle != null && rolle == 1;
    }

    public boolean isWriteRole() {
        return rolle != null && rolle == 2;
    }
}