package com.localnotes.service;

import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.AuthenticationResponse;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.repository.UserRepository;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("h2")
@SpringBootTest
class AuthServiceTest {

    private final static String USER_USERNAME = "TEST_USER";
    private final static String USER_EMAIL = "test@mail.com";
    private final static String USER_PASSWORD = "123456";

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        User user = createUserEntity();
        userRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @DisplayName("При запросе токена по username/password должен вернуть id/token пользователя")
    @Test
    void loginShouldReturnAuthInfo() {
        AuthenticationRequest request = new AuthenticationRequest(USER_USERNAME, USER_PASSWORD);

        AuthenticationResponse login = authService.login(request);

        User savedEntity = userRepository.findByUsername(USER_USERNAME)
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(savedEntity.getPublicId(), login.getId());
    }

    @DisplayName("При запросе токена несуществующего пользователя, должно быть выброшено исключение")
    @Test
    void loginUserNotExists() {
        UserSecurityDto user = createSecurityUser();
        user.setUsername("TEST_USER2");

        AuthenticationRequest request = new AuthenticationRequest(user.getUsername(), user.getPassword());

        Assertions.assertThrows(InternalAuthenticationServiceException.class,
                () -> authService.login(request));
    }

    @DisplayName("Создание нового пользователя, проверка БД, что количество записей изменилось")
    @Test
    void createUser() {
        UserSecurityDto user = createSecurityUser();
        user.setUsername("TEST_USER2");
        user.setEmail("test2@email.com");

        authService.createUser(user);

        Assertions.assertEquals(2, userRepository.findAll().size());
    }

    @DisplayName("Создание нового пользователя, должно быть выброшено исключение, что такой username существет")
    @Test
    void createUserExistsUsername() {
        UserSecurityDto userCopy = createSecurityUser();
        Assertions.assertThrows(IllegalArgumentException.class, () -> authService.createUser(userCopy));
    }

    @DisplayName("Создание нового пользователя, должно быть выброшено исключение, что такой email существет")
    @Test
    void createUserExistsEmail() {
        UserSecurityDto userCopy = createSecurityUser();
        userCopy.setUsername("TEST_USER2");
        Assertions.assertThrows(IllegalArgumentException.class, () -> authService.createUser(userCopy));
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
        user.setEmail(USER_EMAIL);
        user.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));
        user.setStatus(Status.ACTIVE);

        return user;
    }
}