package com.localnotes.repository;

import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByPublicId(String id);

    Optional<List<Note>> findAllByUserId(String userId);

    Optional<List<Note>> findAllByUserIdAndCategory(String userId, Category category);

}
