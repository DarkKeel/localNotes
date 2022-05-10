package com.localnotes.mapper;

import com.localnotes.dto.CreateNoteRequest;
import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import com.localnotes.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoteMapperTest {

    @Autowired
    private NoteMapper noteMapper;

    @Test
    void toNoteEntity() {
        CreateNoteRequest createNoteRequest = new CreateNoteRequest();
        createNoteRequest.setName("TEST");
        createNoteRequest.setDescription("TEST_DESC");
        createNoteRequest.setFavorite(true);
        createNoteRequest.setUserId("USER_ID");

        Note entity = noteMapper.toNoteEntity(createNoteRequest);

        Assertions.assertEquals(createNoteRequest.getName(), entity.getName());
        Assertions.assertEquals(createNoteRequest.getDescription(), entity.getDescription());
        Assertions.assertEquals(createNoteRequest.getFavorite(), entity.isFavorite());
        Assertions.assertEquals(createNoteRequest.getUserId(), entity.getUserId());
    }

    @Test
    void toNoteDto() {
        Note entity = new Note();
        entity.setPublicId("PUBLIC_ID");
        entity.setName("TEST");
        entity.setDescription("TEST_DESC");
        entity.setFavorite(true);
        entity.setUserId("USER_ID");
        Category category = new Category();
        category.setId(1L);
        category.setName("a");
        category.setDescription("b");
        category.setPublicId("PUBLIC_ID_CAT");
        category.setNoteList(Collections.emptyList());
        category.setStatus(Status.ACTIVE);
        category.setColor("#FFFFFF");
        entity.setCategory(category);

        NoteDto dto = noteMapper.toNoteDto(entity);

        Assertions.assertEquals(entity.getPublicId(), dto.getId());
        Assertions.assertEquals(entity.getName(), dto.getName());
        Assertions.assertEquals(entity.getDescription(), dto.getDescription());
        Assertions.assertEquals(entity.isFavorite(), dto.getFavorite());
        Assertions.assertEquals(entity.getUserId(), dto.getUserId());
    }
}