package com.example.demo.controller;

import com.example.demo.entity.Note;
import com.example.demo.entity.ShareNote;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.SharedNoteRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/shared")
public class SharedNoteController {

    @Autowired
    private SharedNoteRepository sharedNoteRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Eine Note mit einem anderen User teilen
     * POST /api/shared/share
     */
@PostMapping("/share")
public ResponseEntity<?> shareNote(@RequestBody Map<String, String> request, Authentication auth) {
    User currentUser = (User) auth.getPrincipal();
    
    // Check if noteId is present to avoid NullPointerException
    if (!request.containsKey("noteId") || !request.containsKey("email")) {
        return ResponseEntity.badRequest().body("Fehlende Parameter: noteId oder email");
    }

    UUID noteId = UUID.fromString(request.get("noteId"));
    String targetEmail = request.get("email");

    if (currentUser.getEmail().equalsIgnoreCase(targetEmail)) {
    return ResponseEntity.badRequest().body(Map.of("error", "Du kannst keine Notiz mit dir selbst teilen."));
}

    // 1. Notiz suchen und Besitz prüfen
    Optional<Note> noteOpt = noteRepository.findByNotizId(noteId);
    if (noteOpt.isEmpty() || !noteOpt.get().getOwner().equals(currentUser.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Du darfst diese Note nicht teilen.");
    }

    // 2. Ziel-User suchen und das Mapping korrekt abschließen
    return userRepository.findByEmail(targetEmail)
        .map(targetUser -> {
            // ERSTER PFAD: User gefunden -> Erfolg
            ShareNote share = new ShareNote();
            share.setNoteId(noteId);
            share.setSharedWithUserId(targetUser.getId());
            share.setSenderId(currentUser.getId());

            sharedNoteRepository.save(share);
            
            // WICHTIG: Hier ein ResponseEntity zurückgeben
            return ResponseEntity.ok(Map.of("message", "Note erfolgreich geteilt!"));
        }) 
        .orElseGet(() -> {
            // ZWEITER PFAD: User nicht gefunden -> Fehler
            // WICHTIG: Auch hier MUSS ein ResponseEntity zurückgegeben werden
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error", "User nicht gefunden"));
        });
}

    /**
     * Alle Notizen abrufen, die mit mir geteilt wurden
     * GET /api/shared/with-me
     */
    @GetMapping("/with-me")
    public ResponseEntity<?> getSharedWithMe(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();

        try {
            List<ShareNote> shares = sharedNoteRepository.findBySharedWithUserId(currentUser.getId());
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (ShareNote share : shares) {
                noteRepository.findByNotizId(share.getNoteId()).ifPresent(note -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("note", note);
                    map.put("sharedBy", userRepository.findById(share.getSenderId()));
                    result.add(map);
                });
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Laden.");
        }
    }
}