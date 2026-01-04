package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "notiz", schema = "public")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notiz_id")
    private Long notizId;

    @Column(name = "title", length = 50)
    @Size(max = 50, message = "Titel darf maximal 50 Zeichen lang sein")
    private String title;

    @Column(name = "notiz_text", length = 500)
    @NotBlank(message = "Notiz-Text darf nicht leer sein")
    @Size(max = 500, message = "Notiz darf maximal 500 Zeichen lang sein")
    private String notizText;

    @Column(name = "is_privat", nullable = false)
    private Boolean isPrivat = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Konstruktor
    public Note() {
        this.createdAt = LocalDateTime.now();
        this.isPrivat = false;
    }

    // Getters and Setters
    public Long getNotizId() {
        return notizId;
    }

    public void setNotizId(Long notizId) {
        this.notizId = notizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotizText() {
        return notizText;
    }

    public void setNotizText(String notizText) {
        this.notizText = notizText;
    }

    public Boolean getIsPrivat() {
        return isPrivat;
    }

    public void setIsPrivat(Boolean isPrivat) {
        this.isPrivat = isPrivat;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Security Helper-Methoden
    public boolean isOwnedBy(User user) {
        return this.owner != null && this.owner.getId().equals(user.getId());
    }

    public boolean canBeViewedBy(User user) {
        // Öffentliche Notizen können von allen gesehen werden
        if (!this.isPrivat) {
            return true;
        }
        // Private Notizen nur vom Owner
        return isOwnedBy(user);
    }

    public boolean canBeEditedBy(User user) {
        // Nur Owner kann bearbeiten
        return isOwnedBy(user);
    }
}