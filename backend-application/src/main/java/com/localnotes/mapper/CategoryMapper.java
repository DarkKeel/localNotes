package com.localnotes.mapper;

import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateCategoryRequest;
import com.localnotes.entity.Category;
import com.localnotes.entity.Status;
import com.localnotes.service.IdService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toCategoryEntity(CreateCategoryRequest dto) {
        Category entity = new Category();
        entity.setPublicId(IdService.createUuid());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUserId(dto.getUserId());
        entity.setCreated(LocalDateTime.now());
        entity.setUpdated(LocalDateTime.now());
        entity.setColor("#FFFFFF");
        entity.setNoteList(Collections.emptyList());
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    public CategoryDto toCategoryDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getPublicId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setColor(entity.getColor());
        dto.setCountOfNotes(entity.getNoteList().size());

        return dto;
    }

    public List<CategoryDto> toCategoryDtoList(List<Category> entityList) {
        return entityList.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }
}
