package com.localnotes.dto;

import com.localnotes.model.Category;
import com.localnotes.repository.CategoryRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class CategoryDto {

    private String id;
    private String name;
    @Autowired
    private CategoryRepository categoryRepository;

    public Category toCategory() {
        Category category = categoryRepository.findByPublicId(id).orElse(null);
        if (category == null) {
            category = new Category();
        }
        category.setName(name);

        return category;
    }

    public static CategoryDto fromCategory(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.id = category.getPublicId();
        categoryDto.name = category.getName();

        return categoryDto;
    }
}
