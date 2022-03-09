package com.localnotes.repository;

import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByPublicId(String id);

    Optional<List<Note>> findAllByUserId(String userId);

    @Query("select n from Note n where n.userId = :userId and n.category = :category and n.isFavorite = true")
    Optional<List<Note>> findAllFavoriteNotes(String userId, Category category);

    void deleteByPublicId(String publicId);
}
