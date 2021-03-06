package com.localnotes.controller;

import com.localnotes.dto.CreateNoteRequest;
import com.localnotes.dto.NoteDto;
import com.localnotes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/{userId}")
    public List<NoteDto> getAllNotes(@PathVariable String userId) {
        return noteService.getAllNotes(userId);
    }

    @GetMapping("/{userId}/{categoryName}")
    public List<NoteDto> getAllNotesByCategory(@PathVariable String userId,
                                               @PathVariable String categoryName) {
        return noteService.getNotesByCategory(userId, categoryName);
    }

    @PostMapping()
    public ResponseEntity<Void> createNote(@RequestBody CreateNoteRequest dto) {
        noteService.createNote(dto);
        return ResponseEntity.created(URI.create("")).build();
    }

    @PutMapping()
    public ResponseEntity<Void> updateNote(@RequestBody NoteDto dto) {
        noteService.updateNote(dto);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{userId}/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable String userId,
                                           @PathVariable String noteId) {
        noteService.deleteNote(noteId, userId);
        return ResponseEntity.ok().build();
    }
}
