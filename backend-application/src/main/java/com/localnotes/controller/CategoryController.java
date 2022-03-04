package com.localnotes.controller;

import com.localnotes.dto.CategoryDto;
import com.localnotes.service.CategoryService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/category")
@Slf4j
public class CategoryController {

    private static final String WRONG_DATA = "Wrong data in request";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{userId}")
    public List<CategoryDto> getCategories(@PathVariable String userId) {
        return categoryService.getCategories(userId);
    }

    @PostMapping("/{userId}")
    public CategoryDto createCategory(@PathVariable String userId, @RequestBody CategoryDto dto) {
        if (!userId.equals(dto.getUserId())) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        log.info("CategoryController: createCategory: creating category for user id: {}", userId);
        return categoryService.createCategory(dto);
    }

    @PutMapping("/{userId}/{categoryId}")
    public CategoryDto updateCategory(@PathVariable String userId,
                                      @PathVariable String categoryId,
                                      @RequestBody CategoryDto dto) {
        if (!userId.equals(dto.getUserId()) || !categoryId.equals(dto.getId())) {
            throw new IllegalArgumentException(WRONG_DATA);
        }
        log.info("CategoryController: updateCategory: updating category id: {}, for user id: {}",
                categoryId, userId);

        return categoryService.updateCategory(dto);
    }

    @DeleteMapping("/{userId}/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable String userId, @PathVariable String categoryId) {
        log.info("CategoryController: deleteCategory: deleting category id: {}, for user id: {}",
                categoryId, userId);
        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.ok("Successfully deleted");
    }
}
