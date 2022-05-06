package com.localnotes.service;

import com.localnotes.dto.CategoryDto;
import com.localnotes.entity.Category;
import com.localnotes.mapper.CategoryMapper;
import com.localnotes.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityNotFoundException;

import com.localnotes.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService {

    private static final String WRONG_DATA = "Wrong data in request";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final NoteRepository noteRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                           NoteRepository noteRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.noteRepository = noteRepository;
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
            result.sort(Comparator.comparing(CategoryDto::getName));

            return result;
        }

        return new ArrayList<>();
    }

    public CategoryDto createCategory(String userId, CategoryDto dto) {
        if (!userId.equals(dto.getUserId())) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        log.info("CategoryService: createCategory: creating category for user id: {}", userId);
        if (categoryRepository.findByNameAndUserId(dto.getName(), dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Category with name: " + dto.getName() + " already exists");
        }
        Category entity = categoryMapper.toCategoryEntity(dto);

        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    public CategoryDto updateCategory(String userId, String categoryId, CategoryDto dto) {
        if (!userId.equals(dto.getUserId()) || !categoryId.equals(dto.getId())) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        log.info("CategoryService: updateCategory: updating category id: {}, for user id: {}",
                categoryId, userId);
        Category entity = categoryRepository.findByNameAndUserId(dto.getName(), dto.getUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Category with name: " + dto.getName() + " doesn't exists"));
        if (!entity.getPublicId().equals(dto.getId())) {
            throw new IllegalArgumentException("The same category is already exsits");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setColor(dto.getColor());
        entity.setCountOfnotes(
                noteRepository.getCountNotesForCategory(dto.getUserId(), categoryMapper.toCategoryEntity(dto)));

        return categoryMapper.toCategoryDto(categoryRepository.save(entity));
    }

    public void deleteCategory(String categoryId, String userId) {
        log.info("CategoryService: deleteCategory: deleting category id: {}, for user id: {}",
                categoryId, userId);
        Category category = categoryRepository.findByPublicIdAndAndUserId(categoryId, userId).orElseThrow(() ->
                new EntityNotFoundException("Category with id: " + categoryId + " doesn't exsist"));
        categoryRepository.delete(category);
    }
}
