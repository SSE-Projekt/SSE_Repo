package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notiz", schema = "public")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notiz_id")
    private UUID notizId;

    @Column(name = "title", length = 50)
    @Size(max = 50, message = "Titel darf maximal 50 Zeichen lang sein")
    private String title;

    @Column(name = "notiz_text", length = 500)
    @NotBlank(message = "Notiz-Text darf nicht leer sein")
    @Size(max = 500, message = "Notiz darf maximal 500 Zeichen lang sein")
    private String notizText;

    @Column(name = "is_privat", nullable = false)
    private Boolean isPrivat = false;

    // Hier speichern wir die UUID des Besitzers
    @Column(name = "owner_id", nullable = false)
    private UUID owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Note() {
        this.createdAt = LocalDateTime.now();
    }

    // --- GETTER & SETTER ---
    public UUID getNotizId() { return notizId; }
    public void setNotizId(UUID notizId) { this.notizId = notizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotizText() { return notizText; }
    public void setNotizText(String notizText) { this.notizText = notizText; }

    public Boolean getIsPrivat() { return isPrivat; }
    public void setIsPrivat(Boolean isPrivat) { this.isPrivat = isPrivat; }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- SECURITY HELPER (KORRIGIERT) ---

    public boolean isOwnedBy(User user) {
        // Vergleiche die UUID der Notiz mit der ID des User-Objekts
        return user != null && this.owner != null && this.owner.equals(user.getId());
    }

    public boolean canBeViewedBy(User user) {
        if (!this.isPrivat) return true; // Öffentlich
        return isOwnedBy(user); // Privat: Nur für Owner
    }

    public boolean canBeEditedBy(User user) {
        return isOwnedBy(user);
    }
}