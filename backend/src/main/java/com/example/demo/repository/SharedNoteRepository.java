package com.example.demo.repository;

import com.example.demo.entity.ShareNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SharedNoteRepository extends JpaRepository<ShareNote, Long> {
    // Findet alle Notizen, die mit einem bestimmten User geteilt wurden
    List<ShareNote> findBySharedWithUserId(UUID sharedWithUserId);
} 