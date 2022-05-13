package com.localnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.AuthenticationResponse;
import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateNoteRequest;
import com.localnotes.dto.NoteDto;
import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.repository.CategoryRepository;
import com.localnotes.repository.NoteRepository;
import com.localnotes.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("h2")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
class NoteControllerTest {

    private final static String LOGIN_URL = "/api/v1/auth/login";
    private final static String NOTE_URL = "/api/v1/note";

    private final static String USER_ID = "PUBUSER";
    private final static String USER_NAME = "TESTUSER";
    private final static String USER_EMAIL = "test@mail.com";
    private final static String USER_PASSWORD = "123456";

    private final static String NOTE_ID = "NOTEPUBID";
    private final static String NOTE_NAME = "Test note";
    private final static String NOTE_DESCRIPTION = "Test note description";

    private final static String CAT_ID = "PUBLICID";
    private final static String CAT_NAME = "test category entity";
    private final static String CAT_DESCRIPTION = "test category entity description";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private User testUser;
    private Category testCategory;
    private Note testNote;
    @Value("${jwt.token.prefix}")
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        prepareData();
    }

    @AfterEach
    void cleanUp() {
        noteRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Получение списка заметок по publicId пользователя, должен вернуть 200 и список заметок")
    @Test
    void getAllNotesShouldReturn200AndList() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get(NOTE_URL + "/" + testUser.getPublicId())
                .header("Authorization", token);
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        NoteDto[] notes = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), NoteDto[].class);
        NoteDto noteDto = notes[0];

        Assertions.assertEquals(noteRepository.findAll().size(), notes.length);
        Assertions.assertEquals(NOTE_ID, noteDto.getId());
        Assertions.assertEquals(NOTE_NAME, noteDto.getName());
        Assertions.assertEquals(NOTE_DESCRIPTION, noteDto.getDescription());
        Assertions.assertEquals(CAT_ID, noteDto.getCategory().getId());
        Assertions.assertEquals(USER_ID, noteDto.getUserId());
    }

    @DisplayName("Получение списка заметок по publicId пользователя и имени категории, должен вернуть 200 и список заметок")
    @Test
    void getAllNotesByCategoryShouldReturn200AndList() throws Exception{
        createAdditionalData();

        RequestBuilder request = MockMvcRequestBuilders
                .get(NOTE_URL + "/" + testUser.getPublicId() + "/" + CAT_NAME)
                .header("Authorization", token);
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        NoteDto[] notes = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), NoteDto[].class);
        NoteDto noteDto = notes[0];

        Assertions.assertNotEquals(noteRepository.findAll().size(), notes.length);
        Assertions.assertEquals(NOTE_ID, noteDto.getId());
        Assertions.assertEquals(NOTE_NAME, noteDto.getName());
        Assertions.assertEquals(NOTE_DESCRIPTION, noteDto.getDescription());
        Assertions.assertEquals(CAT_ID, noteDto.getCategory().getId());
        Assertions.assertEquals(USER_ID, noteDto.getUserId());
    }

    @DisplayName("Создание новой заметки, должен вернуть 201")
    @Test
    void createNoteShouldReturn201() throws Exception {
        String name = "New test note name";
        String desc = "New test note desc";
        CreateNoteRequest noteRequest = createNoteRequest(name, desc);

        byte[] requestBody = objectMapper.writeValueAsBytes(noteRequest);
        RequestBuilder request = MockMvcRequestBuilders.post(NOTE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Assertions.assertEquals(2, noteRepository.findAll().size());
    }

    @DisplayName("Обновление заметки, должен вернуть 202")
    @Test
    void updateNoteShouldReturn202() throws Exception {
        Note noteEntity = noteRepository.findByPublicId(NOTE_ID).get();
        NoteDto noteDto = toNoteDto(noteEntity);
        noteDto.setName("New name of note");

        Assertions.assertNotEquals(noteDto.getName(), noteEntity.getName());

        byte[] requestBody = objectMapper.writeValueAsBytes(noteDto);
        RequestBuilder request = MockMvcRequestBuilders.put(NOTE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isAccepted());

        noteEntity = noteRepository.findByPublicId(NOTE_ID).get();

        Assertions.assertEquals(noteDto.getName(), noteEntity.getName());
    }

    @DisplayName("Обновление несуществующей заметки, должен выбросить исключение")
    @Test
    void updateNoteNotExists() throws Exception {
        Note noteEntity = noteRepository.findByPublicId(NOTE_ID).get();
        NoteDto noteDto = toNoteDto(noteEntity);
        String newId = "NewPublicId";
        noteDto.setId(newId);

        byte[] requestBody = objectMapper.writeValueAsBytes(noteDto);
        RequestBuilder request = MockMvcRequestBuilders.put(NOTE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("Note id: " + newId + " is not found")));
    }

    @DisplayName("Удаление заметки, должен вернуть 200")
    @Test
    void deleteNoteShouldReturn200() throws Exception {
        Assertions.assertEquals(1, noteRepository.findAll().size());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(NOTE_URL + "/" + testUser.getPublicId() + "/" + NOTE_ID)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isOk());

        Assertions.assertEquals(0, noteRepository.findAll().size());
    }

    @DisplayName("Удаление несуществующей заметки, должен выбросить исключение")
    @Test
    void deleteNoteNotExists() throws Exception {
        String notExistsId = "NotExists";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(NOTE_URL + "/" + testUser.getPublicId() + "/" + notExistsId)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("Note id: " + notExistsId + " is not found")));
    }

    @DisplayName("Удаление заметки другим пользователем, должен выбросить исключение")
    @Test
    void deleteNoteNotOwner() throws Exception {
        String notOwnerId = "NotExists";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(NOTE_URL + "/" + notOwnerId + "/" + NOTE_ID)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User id: " + notOwnerId + " is not owner of note: " + NOTE_ID)));
    }

    private void prepareData() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setPublicId(USER_ID);
        user.setUsername(USER_NAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));
        user.setStatus(Status.ACTIVE);

        testUser = userRepository.save(user);

        Category category = new Category();
        category.setId(1L);
        category.setName(CAT_NAME);
        category.setDescription(CAT_DESCRIPTION);
        category.setPublicId(CAT_ID);
        category.setNoteList(Collections.emptyList());
        category.setUserId(testUser.getPublicId());
        category.setStatus(Status.ACTIVE);
        category.setNoteList(Collections.emptyList());

        Note note = new Note();
        note.setId(1L);
        note.setName(NOTE_NAME);
        note.setDescription(NOTE_DESCRIPTION);
        note.setPublicId(NOTE_ID);
        note.setCategory(category);
        note.setUserId(user.getPublicId());
        note.setStatus(Status.ACTIVE);

        testNote = noteRepository.save(note);

        testCategory = categoryRepository.findByPublicId(CAT_ID).get();

        token += getToken();
    }

    private String getToken() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER_NAME, USER_PASSWORD);
        byte[] requestBody = objectMapper.writeValueAsBytes(authenticationRequest);
        RequestBuilder request = MockMvcRequestBuilders.post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        MvcResult result = mockMvc.perform(request).andReturn();
        AuthenticationResponse authenticationResponse =
                objectMapper.readValue(result.getResponse().getContentAsByteArray(), AuthenticationResponse.class);
        return authenticationResponse.getToken();
    }

    private void createAdditionalData() throws Exception {
        Category category = new Category();
        category.setId(2L);
        category.setName("2");
        category.setDescription("2");
        category.setPublicId("2");
        category.setNoteList(Collections.emptyList());
        category.setUserId(testUser.getPublicId());
        category.setStatus(Status.ACTIVE);

        categoryRepository.save(category);

        Note note = new Note();
        note.setId(2L);
        note.setName(NOTE_NAME + "2");
        note.setDescription(NOTE_DESCRIPTION + "2");
        note.setPublicId(NOTE_ID + "2");
        note.setCategory(category);
        note.setUserId(USER_ID);
        note.setStatus(Status.ACTIVE);

        noteRepository.save(note);
    }

    private CreateNoteRequest createNoteRequest(String name, String desc) {
        CreateNoteRequest createNoteRequest = new CreateNoteRequest();
        createNoteRequest.setUserId(USER_ID);
        createNoteRequest.setName(name);
        createNoteRequest.setDescription(desc);
        createNoteRequest.setFavorite(true);
        CategoryDto categoryDto = toCategoryDto(testCategory);

        createNoteRequest.setCategory(categoryDto);
        return createNoteRequest;
    }

    private NoteDto toNoteDto(Note entity) {
        NoteDto dto = new NoteDto();
        dto.setId(entity.getPublicId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUserId());
        dto.setFavorite(entity.isFavorite());
        dto.setStatus(entity.getStatus());
        dto.setCategory(toCategoryDto(testCategory));

        return dto;
    }

    private CategoryDto toCategoryDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getPublicId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setUserId(USER_ID);
        dto.setStatus(entity.getStatus());

        return dto;
    }

}