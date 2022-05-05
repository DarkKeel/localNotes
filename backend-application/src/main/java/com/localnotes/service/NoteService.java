package com.localnotes.service;

import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import com.localnotes.mapper.NoteMapper;
import com.localnotes.repository.NoteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private static final String WRONG_DATA = "There is bad data in input.";

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public List<NoteDto> getAllNotes(String userId) {
        List<Note> result = noteRepository.findAllByUserId(userId).orElse(new ArrayList<>());
        return result.stream().map(noteMapper::toNoteDto).collect(Collectors.toList());
    }

    public List<NoteDto> getFavoriteNotes(String userId, Category category) {
        List<Note> result = noteRepository.findAllFavoriteNotes(userId, category).orElse(new ArrayList<>());
        return result.stream().map(noteMapper::toNoteDto).collect(Collectors.toList());
    }

    public NoteDto createNote(NoteDto dto, String userId) {
        if (dto.getId() == null) {
            return noteMapper.toNoteDto(noteRepository.save(noteMapper.toNoteEntity(dto, userId)));
        }
        throw new IllegalArgumentException("Note with id: " + dto.getId() + " already exists.");
    }

    public NoteDto updateNote(NoteDto dto, String userId) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        Note entity = noteRepository.findByPublicId(dto.getId()).orElseThrow(() ->
                new EntityNotFoundException("Note id: " + dto.getId() + " note found."));
        if (!entity.getUserId().equals(userId)) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        return noteMapper.toNoteDto(noteRepository.save(noteMapper.toNoteEntity(dto, userId)));
    }

    @Transactional
    public void deleteNote(String noteId, String userId) {
        Note entity = noteRepository.findByPublicId(noteId).orElseThrow(() ->
                new EntityNotFoundException("Note id: " + noteId + " note found."));
        if (entity.getUserId().equals(userId)) {
            noteRepository.deleteByPublicId(noteId);
        } else {
            throw new IllegalArgumentException(WRONG_DATA);
        }
    }
}
