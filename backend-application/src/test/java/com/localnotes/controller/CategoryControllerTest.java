package com.localnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.AuthenticationResponse;
import com.localnotes.dto.CategoryDto;
import com.localnotes.dto.CreateCategoryRequest;
import com.localnotes.entity.Category;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.repository.CategoryRepository;
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
class CategoryControllerTest {

    private final static String LOGIN_URL = "/api/v1/auth/login";
    private final static String CATEGORY_URL = "/api/v1/category";

    private final static String USER_ID = "PUBUSER";
    private final static String USER_NAME = "TESTUSER";
    private final static String USER_EMAIL = "test@mail.com";
    private final static String USER_PASSWORD = "123456";

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private User testUser;
    @Value("${jwt.tokenPrefix}")
    private String token;


    @BeforeEach
    void setUp() throws Exception {
        prepareData();
    }

    @AfterEach
    void cleanUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Получение списка категорий по publicId пользователя, должен вернуть 200 и список категорий")
    @Test
    void getCategoriesReturnsListOfCategories() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORY_URL + "/" + testUser.getPublicId())
                .header("Authorization", token);
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] categories = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), CategoryDto[].class);
        CategoryDto resultCategory = categories[0];

        Assertions.assertEquals(CAT_ID, resultCategory.getId());
        Assertions.assertEquals(CAT_NAME, resultCategory.getName());
        Assertions.assertEquals(CAT_DESCRIPTION, resultCategory.getDescription());
        Assertions.assertEquals(USER_ID, resultCategory.getUserId());
    }

    @DisplayName("Создание категории, должен вернуть 201")
    @Test
    void createCategory() throws Exception {
        CreateCategoryRequest requestCategory = createCategoryRequest("new category", "test");

        byte[] requestBody = objectMapper.writeValueAsBytes(requestCategory);
        RequestBuilder request = MockMvcRequestBuilders.post(CATEGORY_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @DisplayName("Создание категории с одинаковым именем, должен выбросить исключение")
    @Test
    void createCategoryDuplicateException() throws Exception {
        CreateCategoryRequest requestCategory = createCategoryRequest(CAT_NAME, CAT_DESCRIPTION);

        byte[] requestBody = objectMapper.writeValueAsBytes(requestCategory);
        RequestBuilder request = MockMvcRequestBuilders.post(CATEGORY_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("Category with name: " + CAT_NAME + " already exists")));
    }

    @DisplayName("Обновление категории, должен вернуть 202")
    @Test
    void updateCategoryShouldReturn202() throws Exception {
        Category categoryEntity = categoryRepository.findByPublicId(CAT_ID).get();
        CategoryDto categoryDto = createCategoryDto(categoryEntity);
        String newNameOfCat = "NewTestName2";
        categoryDto.setName(newNameOfCat);

        Assertions.assertEquals(CAT_NAME, categoryEntity.getName());

        byte[] requestBody = objectMapper.writeValueAsBytes(categoryDto);
        RequestBuilder request = MockMvcRequestBuilders.put(CATEGORY_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isAccepted());

        categoryEntity = categoryRepository.findByPublicId(CAT_ID).get();

        Assertions.assertEquals(newNameOfCat, categoryEntity.getName());
    }

    @DisplayName("Обновление несуществующей категории, должен выбросить исключение")
    @Test
    void updateCategoryNotExists() throws Exception {
        Category categoryEntity = categoryRepository.findByPublicId(CAT_ID).get();
        CategoryDto categoryDto = createCategoryDto(categoryEntity);
        String newPublicId = "NEWID";
        categoryDto.setId(newPublicId);

        byte[] requestBody = objectMapper.writeValueAsBytes(categoryDto);
        RequestBuilder request = MockMvcRequestBuilders.put(CATEGORY_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("Category with id: " + newPublicId + " doesn't exists")));
    }

    @DisplayName("Удаление категории, должен вернуть 200")
    @Test
    void deleteCategoryShouldReturn200() throws Exception {
        Assertions.assertEquals(1, categoryRepository.findAll().size());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(CATEGORY_URL + "/" + testUser.getPublicId() + "/" + CAT_ID)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isOk());

        Assertions.assertEquals(0, categoryRepository.findAll().size());
    }

    @DisplayName("Удаление несуществующей категории, должен выбросить исключение")
    @Test
    void deleteCategoryNotExists() throws Exception {

        String notExistsCategory = "NOCATEGORY";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(CATEGORY_URL + "/" + testUser.getPublicId() + "/" + notExistsCategory)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("Category with id: " + notExistsCategory + " doesn't exsist")));
    }

    @DisplayName("Удаление категории другим пользователем, должен выбросить исключение")
    @Test
    void deleteCategoryNotOwner() throws Exception {

        String notOwnerId = "NOTOWNER";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(CATEGORY_URL + "/" + notOwnerId + "/" + CAT_ID)
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User id: " + notOwnerId + " is not owner of category: " + CAT_ID)));
    }

    private CategoryDto createCategoryDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getPublicId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUserId());
        dto.setStatus(entity.getStatus());
        dto.setCountOfNotes(0);

        return dto;
    }

    private CreateCategoryRequest createCategoryRequest(String name, String desc) {
        CreateCategoryRequest categoryRequest  = new CreateCategoryRequest();
        categoryRequest.setUserId(testUser.getPublicId());
        categoryRequest.setName(name);
        categoryRequest.setDescription(desc);

        return categoryRequest;
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

        categoryRepository.save(category);

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
}