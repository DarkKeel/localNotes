package com.localnotes.mapper;

import com.localnotes.dto.CategoryDto;
import com.localnotes.entity.Category;
import com.localnotes.repository.CategoryRepository;
import com.localnotes.service.IdService;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category toCategoryEntity(CategoryDto dto) {
        Category entity = categoryRepository.findByPublicId(dto.getId())
                .orElse(null);
        if (entity == null) {
            entity = new Category();
            entity.setPublicId(IdService.createUuid());
            entity.setCreated(new Date());
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUserId(dto.getUserId());
        entity.setStatus(dto.getStatus());
        entity.setUpdated(new Date());
        entity.setCountOfnotes(dto.getCountOfnotes());
        entity.setColor(dto.getColor());

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
        dto.setCountOfnotes(entity.getCountOfnotes());

        return dto;
    }
}
