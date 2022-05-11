package com.localnotes.service;

import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateCategoryRequest;
import com.localnotes.entity.Category;
import com.localnotes.mapper.CategoryMapper;
import com.localnotes.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Category getCategoryByName(String categoryName, String userId) {
        return categoryRepository.findByNameAndUserId(categoryName, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category with name: " + categoryName + " doesn't exists"));
    }

    public List<CategoryDto> getCategories(String userId) {
        List<Category> entityList = categoryRepository.findAllByUserId(userId);
        if (entityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<CategoryDto> result = new ArrayList<>();
        entityList.forEach(category -> {
            CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
            result.add(categoryDto);
        });
        result.sort(Comparator.comparing(CategoryDto::getName));

        return result;
    }

    public void createCategory(CreateCategoryRequest dto) {
        log.info("CategoryService: createCategory: creating category for user: {}", dto.getUserId());
        if (categoryRepository.findByNameAndUserId(dto.getName(), dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Category with name: " + dto.getName() + " already exists");
        }
        Category entity = categoryMapper.toCategoryEntity(dto);
        categoryRepository.save(entity);
    }

    public void updateCategory(CategoryDto dto) {
        log.info("CategoryService: updateCategory: updating category id: {}, for user id: {}",
                dto.getId(), dto.getUserId());
        Category entity = categoryRepository.findByPublicId(dto.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Category with id: " + dto.getId() + " doesn't exists"));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setColor(dto.getColor());
        entity.setUpdated(LocalDateTime.now());
        categoryRepository.save(entity);
    }

    public void deleteCategory(String categoryId, String userId) {
        log.info("CategoryService: deleteCategory: deleting category id: {}, for user id: {}",
                categoryId, userId);
        Category category = categoryRepository.findByPublicId(categoryId).orElseThrow(() ->
                new EntityNotFoundException("Category with id: " + categoryId + " doesn't exsist"));
        if (userId.equals(category.getUserId())) {
            categoryRepository.delete(category);
        } else {
            throw new IllegalArgumentException("User id: " + userId + " is not owner of category: " + categoryId);
        }
    }

    public Category getCategory(String publicId) {
        return categoryRepository.findByPublicId(publicId).orElseThrow();
    }
}
