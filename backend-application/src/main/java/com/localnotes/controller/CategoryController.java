package com.localnotes.controller;

import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateCategoryRequest;
import com.localnotes.service.CategoryService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{userId}")
    public List<CategoryDto> getCategories(@PathVariable String userId) {
        return categoryService.getCategories(userId);
    }

    @PostMapping()
    public ResponseEntity<Void> createCategory(@RequestBody CreateCategoryRequest dto) {
        categoryService.createCategory(dto);
        return ResponseEntity.created(URI.create("")).build();
    }

    @PutMapping()
    public ResponseEntity<Void> updateCategory(@RequestBody CategoryDto dto) {
        categoryService.updateCategory(dto);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{userId}/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String userId,
                                               @PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.ok().build();
    }
}
