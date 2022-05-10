package com.localnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.User;
import com.localnotes.repository.UserRepository;
import com.localnotes.service.IdService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
class AuthenticationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        UserSecurityDto user = new UserSecurityDto();
        user.setId(IdService.createUuid());
        user.setUsername("testUser");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test@email.ru");
        user.setPassword("123456");

        byte[] requestBody = objectMapper.writeValueAsBytes(user);
        String postUrlString = "/api/v1/auth/create";
        RequestBuilder request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request);

        AuthenticationRequest authenticationRequestDto = new AuthenticationRequest();
        authenticationRequestDto.setUsername(user.getUsername());
        authenticationRequestDto.setPassword(user.getPassword());

        requestBody = objectMapper.writeValueAsBytes(authenticationRequestDto);
        postUrlString = "/api/v1/auth/login";
        request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userRepository.findByUsername(user.getUsername()).get().getPublicId()))
                .andExpect(jsonPath("$.token").isNotEmpty());

    }

    @Test
    void loginUserNotExists() throws Exception {
        User user = new User();
        user.setId(1l);
        user.setUsername("testUser");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test@email.ru");
        user.setPassword("123456");

        AuthenticationRequest authenticationRequestDto = new AuthenticationRequest();
        authenticationRequestDto.setUsername(user.getUsername());
        authenticationRequestDto.setPassword(user.getPassword());

        byte[] requestBody = objectMapper.writeValueAsBytes(authenticationRequestDto);
        String postUrlString = "/api/v1/auth/login";
        RequestBuilder request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        Matchers.containsString("User with username: testUser not found")));

    }

    @Test
    void createUser() throws Exception{
        User user = new User();
        user.setId(1l);
        user.setUsername("testUser");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test@email.ru");
        user.setPassword("123456");

        byte[] requestBody = objectMapper.writeValueAsBytes(user);
        String postUrlString = "/api/v1/auth/create";
        RequestBuilder request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void createUserDuplicateUsername() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test123@email.ru");
        user.setPassword("123456");

        userRepository.save(user);

        byte[] requestBody = objectMapper.writeValueAsBytes(user);
        String postUrlString = "/api/v1/auth/create";
        RequestBuilder request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User with username: testUser already exists")));
    }

    @Test
    void createUserDuplicateEmail() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test123@email.ru");
        user.setPassword("123456");

        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("testUser123");
        user2.setFirstName("FirstName");
        user2.setLastName("LastName");
        user2.setEmail("test123@email.ru");
        user2.setPassword("123456");

        byte[] requestBody = objectMapper.writeValueAsBytes(user2);
        String postUrlString = "/api/v1/auth/create";
        RequestBuilder request = MockMvcRequestBuilders.post(postUrlString)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(
                        Matchers.containsString("User with email: test123@email.ru already exists")));
    }
}