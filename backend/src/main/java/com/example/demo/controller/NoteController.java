package com.example.demo.controller;

import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Alle öffentlichen Notizen abrufen
     * GET /api/notes/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<Note>> getPublicNotes() {
        List<Note> notes = noteRepository.findByIsPrivatFalse();
        return ResponseEntity.ok(notes);
    }

    /**
     * Meine Notizen abrufen (öffentlich + privat)
     * GET /api/notes/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<Note>> getMyNotes(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<Note> notes = noteRepository.findByOwner(currentUser);
        return ResponseEntity.ok(notes);
    }

    /**
     * Eine spezifische Notiz abrufen
     * GET /api/notes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        Note note = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notiz nicht gefunden"));
        
        // Security Check: Darf der User diese Notiz sehen?
        if (!note.canBeViewedBy(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Keine Berechtigung für diese Notiz"));
        }
        
        return ResponseEntity.ok(note);
    }

    /**
     * Neue Notiz erstellen
     * POST /api/notes
     */
    @PostMapping
    public ResponseEntity<?> createNote(
            @Valid @RequestBody Map<String, Object> noteData,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Check: Hat User Schreibrechte (Rolle 2)?
        if (currentUser.getRolle() != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Keine Schreibrechte. Nur Rolle 2 darf Notizen erstellen."));
        }
        
        Note note = new Note();
        note.setTitle((String) noteData.get("title"));
        note.setNotizText((String) noteData.get("notizText"));
        note.setIsPrivat((Boolean) noteData.getOrDefault("isPrivat", false));
        note.setOwner(currentUser);
        
        Note savedNote = noteRepository.save(note);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    /**
     * Notiz aktualisieren
     * PUT /api/notes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Object> noteData,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        Note note = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notiz nicht gefunden"));
        
        // Security Check: Ist der User der Owner?
        if (!note.canBeEditedBy(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Nur der Ersteller kann diese Notiz bearbeiten"));
        }
        
        // Update
        if (noteData.containsKey("title")) {
            note.setTitle((String) noteData.get("title"));
        }
        if (noteData.containsKey("notizText")) {
            note.setNotizText((String) noteData.get("notizText"));
        }
        if (noteData.containsKey("isPrivat")) {
            note.setIsPrivat((Boolean) noteData.get("isPrivat"));
        }
        
        Note updatedNote = noteRepository.save(note);
        return ResponseEntity.ok(updatedNote);
    }

    /**
     * Notiz löschen
     * DELETE /api/notes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        Note note = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notiz nicht gefunden"));
        
        // Security Check
        if (!note.canBeEditedBy(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Nur der Ersteller kann diese Notiz löschen"));
        }
        
        noteRepository.delete(note);
        
        return ResponseEntity.ok(Map.of("message", "Notiz erfolgreich gelöscht"));
    }

    /**
     * Suche in Notizen
     * GET /api/notes/search?q=keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(
            @RequestParam String q,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Suche in eigenen Notizen + öffentlichen Notizen
        List<Note> myNotes = noteRepository.findByOwnerAndTitleContainingOrOwnerAndNotizTextContaining(
            currentUser, q, currentUser, q
        );
        
        List<Note> publicNotes = noteRepository.findByIsPrivatFalseAndTitleContainingOrNotizTextContaining(
            q, q
        );
        
        // Kombinieren (eigene + öffentliche, keine Duplikate)
        myNotes.addAll(publicNotes);
        
        return ResponseEntity.ok(myNotes);
    }
}