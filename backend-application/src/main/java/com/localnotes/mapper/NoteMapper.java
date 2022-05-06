package com.localnotes.mapper;

import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Note;
import com.localnotes.repository.NoteRepository;
import com.localnotes.service.IdService;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    private final NoteRepository noteRepository;
    private final CategoryMapper categoryMapper;

    public NoteMapper(NoteRepository noteRepository, CategoryMapper categoryMapper) {
        this.noteRepository = noteRepository;
        this.categoryMapper = categoryMapper;
    }

    public Note toNoteEntity(NoteDto dto, String userId) {
        Note entity = noteRepository.findByPublicId(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new Note();
            entity.setPublicId(IdService.createUuid());
            entity.setCreated(new Date());
            entity.setUserId(userId);
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(categoryMapper.toCategoryEntity(dto.getCategory()));
        entity.setStatus(dto.getStatus());
        entity.setFavorite(dto.getFavorite());
        entity.setUpdated(new Date());

        return entity;
    }

    public NoteDto toNoteDto(Note entity) {
        NoteDto dto = new NoteDto();
        dto.setId(entity.getPublicId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategory(categoryMapper.toCategoryDto(entity.getCategory()));
        dto.setStatus(entity.getStatus());
        dto.setFavorite(entity.isFavorite());
        dto.setCreated(entity.getCreated());
        dto.setUpdated(entity.getUpdated());

        return dto;
    }
}
