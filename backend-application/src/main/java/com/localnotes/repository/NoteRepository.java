package com.localnotes.repository;

import com.localnotes.model.Note;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByPublicId(String id);
}
