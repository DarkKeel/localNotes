package com.localnotes.mapper;

import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateNoteRequest;
import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Note;
import com.localnotes.entity.Status;
import com.localnotes.service.IdService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NoteMapper {

    public Note toNoteEntity(CreateNoteRequest dto) {
        Note entity = new Note();
        entity.setPublicId(IdService.createUuid());
        entity.setCreated(LocalDateTime.now());
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(Status.ACTIVE);
        entity.setFavorite(dto.getFavorite());
        entity.setUpdated(LocalDateTime.now());

        return entity;
    }

    public NoteDto toNoteDto(Note entity) {
        NoteDto dto = new NoteDto();
        dto.setId(entity.getPublicId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setFavorite(entity.isFavorite());
        dto.setCreated(entity.getCreated());
        dto.setUpdated(entity.getUpdated());
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(entity.getCategory().getPublicId());
        categoryDto.setName(entity.getCategory().getName());
        categoryDto.setDescription(entity.getCategory().getDescription());
        categoryDto.setUserId(entity.getUserId());
        categoryDto.setStatus(entity.getCategory().getStatus());
        categoryDto.setColor(entity.getCategory().getColor());
        categoryDto.setCountOfNotes(entity.getCategory().getNoteList().size());
        dto.setCategory(categoryDto);

        return dto;
    }
}
