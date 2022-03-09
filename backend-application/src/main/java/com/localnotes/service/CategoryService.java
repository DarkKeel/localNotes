package com.localnotes.service;

import com.localnotes.dto.CategoryDto;
import com.localnotes.entity.Category;
import com.localnotes.mapper.CategoryMapper;
import com.localnotes.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public Category getCategoryByName(String categoryName, String userId) {
        Category entity = categoryRepository.findByNameAndUserId(categoryName, userId).orElseThrow(() ->
                new EntityNotFoundException("Category with name: " + categoryName + " doesn't exists"));
        return entity;
    }

    public List<CategoryDto> getCategories(String userId) {
        List<Category> entityList = new ArrayList<>(categoryRepository.findAllByUserId(userId));
        if (entityList.size() > 0) {
            List<CategoryDto> result = new ArrayList<>();
            entityList.forEach(category -> result.add(categoryMapper.toCategoryDto(category)));

            return result;
        }

        return new ArrayList<>();
    }

    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.findByNameAndUserId(dto.getName(), dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Category with name: " + dto.getName() + " already exists");
        }
        Category entity = categoryMapper.toCategoryEntity(dto);

        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    public CategoryDto updateCategory(CategoryDto dto) {
        Category entity = categoryRepository.findByNameAndUserId(dto.getName(), dto.getUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Category with name: " + dto.getName() + " doesn't exists"));
        if (!entity.getPublicId().equals(dto.getId())) {
            throw new IllegalArgumentException("The same category is already exsits");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());

        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    public void deleteCategory(String id, String userId) {
        Category category = categoryRepository.findByPublicIdAndAndUserId(id, userId).orElseThrow(() ->
                new EntityNotFoundException("Category with id: " + id + " doesn't exsist"));
        categoryRepository.delete(category);
    }
}
