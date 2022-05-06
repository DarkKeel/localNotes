package com.localnotes.controller;

import com.localnotes.dto.CategoryDto;
import com.localnotes.service.CategoryService;
import java.util.List;
import org.springframework.http.HttpStatus;
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
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{userId}")
    public List<CategoryDto> getCategories(@PathVariable String userId) {
        return categoryService.getCategories(userId);
    }

    @PostMapping("/{userId}")
    public CategoryDto createCategory(@PathVariable String userId,
                                      @RequestBody CategoryDto dto) {
        return categoryService.createCategory(userId, dto);
    }

    @PutMapping("/{userId}/{categoryId}")
    public CategoryDto updateCategory(@PathVariable String userId,
                                      @PathVariable String categoryId,
                                      @RequestBody CategoryDto dto) {
        return categoryService.updateCategory(userId, categoryId, dto);
    }

    @DeleteMapping("/{userId}/{categoryId}")
    public ResponseEntity deleteCategory(@PathVariable String userId,
                                         @PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
