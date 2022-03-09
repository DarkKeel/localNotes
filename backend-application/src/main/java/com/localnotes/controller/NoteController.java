package com.localnotes.controller;

import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Category;
import com.localnotes.service.CategoryService;
import com.localnotes.service.NoteService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/note")
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final CategoryService categoryService;

    public NoteController(NoteService noteService, CategoryService categoryService) {
        this.noteService = noteService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{userId}")
    public List<NoteDto> getAllNotes(@PathVariable String userId) {
        return noteService.getAllNotes(userId);
    }

    @GetMapping("/{userId}/{categoryName}")
    public List<NoteDto> getAllFavorite(@PathVariable String userId,
                                        @PathVariable String categoryName) {
        Category category = categoryService.getCategoryByName(categoryName, userId);
        return noteService.getFavoriteNotes(userId, category);
    }

    @PostMapping("/{userId}")
    public NoteDto createNote(@PathVariable String userId,
                              @RequestBody NoteDto dto) {
        log.info("NoteController: createNote: creating note name: '{}' for user id: {}", dto.getName(), userId);
        return noteService.createNote(dto, userId);
    }

    @PutMapping("/{userId}/{noteId}")
    public NoteDto updateNote(@PathVariable String userId,
                              @PathVariable String noteId,
                              @RequestBody NoteDto dto) {
        if (!dto.getId().equals(noteId)) {
            throw new IllegalArgumentException("Wrong data");
        }
        log.info("NoteController: updateNote: updating note id: {} for user id: {}", dto.getId(), userId);
        return noteService.updateNote(dto, userId);
    }

    @DeleteMapping("/{userId}/{noteId}")
    public ResponseEntity deleteNote(@PathVariable String userId,
                                     @PathVariable String noteId) {
        log.info("NoteController: deleteNote: deleting note id: {} for user id: {}", noteId, userId);
        noteService.deleteNote(noteId, userId);
        return ResponseEntity.ok("Successfully deleted");
    }
}
