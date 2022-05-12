package com.localnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("h2")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
class AuthenticationControllerTest {

    private final static String USER_USERNAME = "TEST_USER";
    private final static String USER_EMAIL = "test@email.com";
    private final static String USER_PASSWORD = "123456";

    private final static String LOGIN_URL = "/api/v1/auth/login";
    private final static String CREATE_URL = "/api/v1/auth/create";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        User entity = createUserEntity();
        userRepository.save(entity);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @DisplayName("Авторизация, должен вернуть статус 200 и id/token аккаунта")
    @Test
    void loginShouldReturnStatus200() throws Exception {
        User entity = userRepository.findByUsername(USER_USERNAME).orElseThrow();

        AuthenticationRequest authenticationRequestDto =
                new AuthenticationRequest(USER_USERNAME, USER_PASSWORD);

        byte[] requestBody = objectMapper.writeValueAsBytes(authenticationRequestDto);
        RequestBuilder request = MockMvcRequestBuilders.post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getPublicId()))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @DisplayName("Авторизация, должен вернуть статус 404 и ошибку, что пользователь не найден")
    @Test
    void loginUserNotExists() throws Exception {
        UserSecurityDto user = createSecurityUser();
        user.setUsername("TEST_USER2");

        AuthenticationRequest authenticationRequestDto =
                new AuthenticationRequest(user.getUsername(), user.getPassword());

        byte[] requestBody = objectMapper.writeValueAsBytes(authenticationRequestDto);
        RequestBuilder request = MockMvcRequestBuilders.post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        Matchers.containsString("User with username: TEST_USER2 not found")));
    }

    @DisplayName("Создание пользователя, должен вернуть статус 201 и проверить, что в БД добавлена запись")
    @Test
    void createUser() throws Exception{
        UserSecurityDto user = createSecurityUser();
        user.setUsername("TEST_USER2");
        user.setEmail("test2@email.com");

        byte[] requestBody = objectMapper.writeValueAsBytes(user);
        RequestBuilder request = MockMvcRequestBuilders.post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Assertions.assertEquals(2, userRepository.findAll().size());
    }

    @DisplayName("Создание пользователя, должен вернуть статус 400 и сообщение, " +
            "что пользователь с таким username уже существует")
    @Test
    void createUserDuplicateUsername() throws Exception{
        User user = createUserEntity();

        byte[] requestBody = objectMapper.writeValueAsBytes(user);
        RequestBuilder request = MockMvcRequestBuilders.post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User with username: TEST_USER already exists")));
    }

    @DisplayName("Создание пользователя, должен вернуть статус 400 и сообщение, " +
            "что пользователь с таким email уже существует")
    @Test
    void createUserDuplicateEmail() throws Exception{
        UserSecurityDto user2 = createSecurityUser();
        user2.setUsername("TEST_USER2");

        byte[] requestBody = objectMapper.writeValueAsBytes(user2);
        RequestBuilder request = MockMvcRequestBuilders.post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User with email: test@email.com already exists")));
    }

    private UserSecurityDto createSecurityUser() {
        UserSecurityDto user = new UserSecurityDto();
        user.setUsername(USER_USERNAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);

        return user;
    }

    private User createUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USER_USERNAME);
        user.setStatus(Status.ACTIVE);
        user.setEmail(USER_EMAIL);
        user.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));

        return user;
    }
}