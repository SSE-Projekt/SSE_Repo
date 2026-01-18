package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "shared_notiz") // WICHTIG: Name deiner Tabelle in Supabase
public class ShareNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notiz_id")
    private UUID noteId;

    @Column(name = "receiver_id")
    private UUID sharedWithUserId;

    @Column(name = "sender_id")
    private UUID senderId;

    //@Column(name = "senderName")
    //private String senderName; // Hier speichern wir deinen Namen

    // Standard-Konstruktor (für JPA)
    public ShareNote() {
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public UUID getNoteId() {
        return noteId;
    }

    public void setNoteId(UUID noteId) {
        this.noteId = noteId;
    }

    public UUID getSharedWithUserId() {
        return sharedWithUserId;
    }

    public void setSharedWithUserId(UUID sharedWithUserId) {
        this.sharedWithUserId = sharedWithUserId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID sender_id) {
        this.senderId = sender_id;
    }
}