package com.example.demo.repository;

import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // Alle Notizen eines Users
    List<Note> findByOwner(UUID owner);
    // Notizen über IDs finden
    Optional<Note> findByNotizId(UUID id);
    // Alle öffentlichen Notizen
    List<Note> findByIsPrivatFalse();
    
    // Alle Notizen eines Users (öffentlich oder privat)
    List<Note> findByOwnerAndIsPrivat(User owner, Boolean isPrivat);
    
    // Suche in Titel oder Text (öffentliche Notizen)
    List<Note> findByIsPrivatFalseAndTitleContainingOrNotizTextContaining(
        String titleKeyword, String textKeyword
    );
    
    // Suche in eigenen Notizen
    List<Note> findByOwnerAndTitleContainingOrOwnerAndNotizTextContaining(
        User owner1, String titleKeyword, 
        User owner2, String textKeyword
    );
}