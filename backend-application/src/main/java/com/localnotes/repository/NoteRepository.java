package com.localnotes.repository;

import com.localnotes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByPublicId(String id);

    Optional<List<Note>> findAllByUserId(String userId);

    @Query("select n from Note n where n.userId = :userId and n.category.name = :category and n.category.userId = :userId")
    Optional<List<Note>> findAllByUserIdAndCategoryName(String userId, String category);

}
