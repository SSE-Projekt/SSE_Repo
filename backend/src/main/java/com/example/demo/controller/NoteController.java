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
import java.util.UUID;

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
    @GetMapping("/hello")
    public String hello() {
        return "Freier Zugriff funktioniert!";
    }
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
    public ResponseEntity<?> getMyNotes(Authentication authentication) {
        // DAS HIER RETTET DICH VOR DEM ABSTURZ:
        if (authentication == null) {
            return ResponseEntity.status(401).body("Nicht autorisiert: Kein gültiger Token gefunden.");
        }
         try{
            User currentUser = (User) authentication.getPrincipal();
            List<Note> notes = noteRepository.findByOwner(currentUser.getId());
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace(); // Das schreibt den Fehler in deine IntelliJ Konsole
            return ResponseEntity.status(500).body("Datenbankfehler: " + e.getMessage());
        }
    }

    /**
     * Eine spezifische Notiz abrufen
     * GET /api/notes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNote(@PathVariable UUID id, Authentication authentication) {
        // 1. Sicherheits-Check: Ist überhaupt jemand eingeloggt?
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Bitte logge dich zuerst ein."));
        }

        // 2. Den aktuellen User aus dem SecurityContext holen
        // Unser Filter legt das User-Objekt als Principal ab
        User currentUser = (User) authentication.getPrincipal();

        // 3. Notiz suchen
        return noteRepository.findByNotizId(id)
                .map(note -> {
                    // 4. Security Check: Darf der User diese Notiz sehen?
                    // Wir nutzen die Methode aus deinem Entity-Modell
                    if (!note.canBeViewedBy(currentUser)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Keine Berechtigung für diese Notiz"));
                    }
                    return ResponseEntity.ok(note);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Notiz mit ID " + id + " nicht gefunden")));
    }

    /**
     * Neue Notiz erstellen
     * POST /api/notes
     */
    @PostMapping
    public ResponseEntity<?> createNote(
            @RequestBody Note noteRequest, // Nutzt Jackson, um JSON direkt in das Objekt zu laden
            Authentication authentication) {

        // 1. Sicherheits-Check
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nicht eingeloggt.");
        }

        User currentUser = (User) authentication.getPrincipal();

        // 2. Rollen-Check (Rolle 2 = Schreibrechte)
        // Hinweis: Stelle sicher, dass getRolle() in deiner User-Entity ein Integer ist
        if (currentUser.getRolle() == null || currentUser.getRolle() != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Keine Schreibrechte. Dein Account hat Rolle: " + currentUser.getRolle()));
        }

        // 3. Daten zuweisen
        Note note = new Note();
        note.setTitle(noteRequest.getTitle());
        note.setNotizText(noteRequest.getNotizText());
        note.setIsPrivat(noteRequest.getIsPrivat() != null ? noteRequest.getIsPrivat() : false);

        // 4. Owner setzen (Wichtig: Die UUID aus dem aktuellen User)
        note.setOwner(currentUser.getId());

        // 5. Speichern
        Note savedNote = noteRepository.save(note);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    /**
     * Notiz aktualisieren
     * PUT /api/notes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable UUID id, // Wichtig: UUID statt Long
            @RequestBody Map<String, Object> noteData,
            Authentication authentication) {

        // 1. Authentifizierung prüfen
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nicht eingeloggt");
        }

        User currentUser = (User) authentication.getPrincipal();

        // 2. Notiz in der DB suchen
        return noteRepository.findByNotizId(id)
                .map(note -> {
                    // 3. Security Check: Darf dieser User editieren?
                    // In deiner Entity: return this.owner.equals(user.getId());
                    if (!note.getOwner().equals(currentUser.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Nur der Ersteller kann diese Notiz bearbeiten"));
                    }

                    // 4. Felder nur aktualisieren, wenn sie im JSON mitgeschickt wurden
                    if (noteData.containsKey("title")) {
                        note.setTitle((String) noteData.get("title"));
                    }
                    if (noteData.containsKey("notizText")) {
                        note.setNotizText((String) noteData.get("notizText"));
                    }
                    if (noteData.containsKey("isPrivat")) {
                        note.setIsPrivat((Boolean) noteData.get("isPrivat"));
                    }

                    // 5. Speichern und Ergebnis zurückgeben
                    Note updatedNote = noteRepository.save(note);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Notiz nicht gefunden")));
    }

    /**
     * Notiz löschen
     * DELETE /api/notes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable UUID id, Authentication authentication) {
        // 1. Sicherheits-Check: Ist der User eingeloggt?
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Bitte logge dich zuerst ein."));
        }

        User currentUser = (User) authentication.getPrincipal();

        // 2. Notiz in der Datenbank suchen
        // Wir nutzen findBynotizId, da dies dein konsistenter Weg für UUIDs ist
        return noteRepository.findByNotizId(id)
                .map(note -> {
                    // 3. Besitz-Check: Nur der Ersteller darf löschen
                    // Hier nutzen wir deine Methode canBeEditedBy (die den Owner-Vergleich macht)
                    if (!note.canBeEditedBy(currentUser)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("error", "Nur der Ersteller kann diese Notiz löschen"));
                    }

                    // 4. Löschvorgang
                    noteRepository.delete(note);

                    return ResponseEntity.ok(Map.of("message", "Notiz erfolgreich gelöscht"));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Notiz mit ID " + id + " nicht gefunden")));
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