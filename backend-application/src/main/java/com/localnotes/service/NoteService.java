package com.localnotes.service;

import com.localnotes.dto.CreateNoteRequest;
import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import com.localnotes.mapper.NoteMapper;
import com.localnotes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final CategoryService categoryService;

    public List<NoteDto> getAllNotes(String userId) {
        List<Note> result = noteRepository.findAllByUserId(userId)
                .orElse(Collections.emptyList());
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result.stream()
                .map(noteMapper::toNoteDto)
                .sorted(Comparator.comparing(NoteDto::getUpdated).reversed())
                .collect(Collectors.toList());
    }

    public List<NoteDto> getNotesByCategory(String userId, String category) {
        List<Note> result = noteRepository.findAllByUserIdAndCategoryName(userId, category)
                .orElse(Collections.emptyList());
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result.stream()
                .map(noteMapper::toNoteDto)
                .sorted(Comparator.comparing(NoteDto::getCreated))
                .sorted(Comparator.comparing(NoteDto::getFavorite).reversed())
                .collect(Collectors.toList());
    }

    public void createNote(CreateNoteRequest dto) {
        log.info("NoteService: createNote: creating note name: '{}' for user id: {}", dto.getName(), dto.getUserId());
        Category category = categoryService.getCategory(dto.getCategory().getId());
        Note entity = noteMapper.toNoteEntity(dto);
        entity.setCategory(category);
        noteRepository.save(entity);
    }

    public void updateNote(NoteDto dto) {
        log.info("NoteService: updateNote: updating note id: {} for user id: {}", dto.getId(), dto.getUserId());

        Note entity = noteRepository.findByPublicId(dto.getId()).orElseThrow(() ->
                new EntityNotFoundException("Note id: " + dto.getId() + " is not found"));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        Category category = categoryService.getCategory(dto.getCategory().getId());
        entity.setCategory(category);
        entity.setFavorite(dto.getFavorite());
        entity.setUpdated(LocalDateTime.now());
        noteRepository.save(entity);
    }

    public void deleteNote(String noteId, String userId) {
        log.info("NoteService: deleteNote: deleting note id: {} for user id: {}", noteId, userId);
        Note entity = noteRepository.findByPublicId(noteId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Note id: " + noteId + " is not found"));
        if (userId.equals(entity.getUserId())) {
            noteRepository.delete(entity);
        } else {
            throw new IllegalArgumentException("User id: " + userId + " is not owner of note: " + noteId);
        }
    }
}
