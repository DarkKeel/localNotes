package com.localnotes.mapper;

import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateCategoryRequest;
import com.localnotes.entity.Category;
import com.localnotes.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

@ActiveProfiles("h2")
@SpringBootTest
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @DisplayName("Создание Category из CreateCategoryRequest, проверка полей")
    @Test
    void createCategoryRequestToCategoryEntityMapping() {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        createCategoryRequest.setName("TEST");
        createCategoryRequest.setDescription("TEST_DESCRIPTION");
        createCategoryRequest.setUserId("USER_ID");

        Category entity = categoryMapper.toCategoryEntity(createCategoryRequest);

        Assertions.assertEquals(createCategoryRequest.getName(), entity.getName());
        Assertions.assertEquals(createCategoryRequest.getDescription(), entity.getDescription());
        Assertions.assertEquals(createCategoryRequest.getUserId(), entity.getUserId());
        Assertions.assertEquals("#FFFFFF", entity.getColor());
    }

    @DisplayName("Создание CategoryDto из Category, проверка полей")
    @Test
    void categoryToCategoryDtoMapping() {
        Category entity = new Category();
        entity.setPublicId("PUBLIC_ID");
        entity.setColor("#000000");
        entity.setName("TEST");
        entity.setDescription("TEST_DESC");
        entity.setNoteList(Collections.emptyList());
        entity.setStatus(Status.ACTIVE);
        entity.setUserId("TEST_USER");

        CategoryDto dto = categoryMapper.toCategoryDto(entity);

        Assertions.assertEquals(entity.getPublicId(), dto.getId());
        Assertions.assertEquals(entity.getName(), dto.getName());
        Assertions.assertEquals(entity.getDescription(), dto.getDescription());
        Assertions.assertEquals(entity.getColor(), dto.getColor());
        Assertions.assertEquals(entity.getUserId(), dto.getUserId());
        Assertions.assertEquals(entity.getStatus(), dto.getStatus());
    }
}