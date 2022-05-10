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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void login() {
        User user = new User();
        user.setId(1L);
        user.setUsername("TESTUSER");
        user.setStatus(Status.ACTIVE);
        String password = "123456";
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername(user.getUsername());
        request.setPassword(password);

        AuthenticationResponse login = authService.login(request);

        User savedEntity = userRepository.findByUsername(user.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(savedEntity.getPublicId(), login.getId());
    }

    @Test
    void loginUserNotExists() {
        UserSecurityDto user = new UserSecurityDto();
        user.setId("USER_ID");
        user.setUsername("TESTUSER");
        user.setPassword("123456");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());

        Assertions.assertThrows(InternalAuthenticationServiceException.class,
                () -> authService.login(request));
    }

    @Test
    void createUser() {
        UserSecurityDto user = new UserSecurityDto();
        user.setId("USER_ID");
        user.setUsername("TESTUSER");
        user.setPassword("123456");

        authService.createUser(user);

        Assertions.assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void createUserExistsUsername() {
        UserSecurityDto user = new UserSecurityDto();
        user.setId("USER_ID");
        user.setUsername("TESTUSER");
        user.setPassword("123456");
        authService.createUser(user);
        Assertions.assertEquals(1, userRepository.findAll().size());

        UserSecurityDto userCopy = new UserSecurityDto();
        userCopy.setId("USER_ID2");
        userCopy.setUsername("TESTUSER");
        userCopy.setPassword("123456");
        Assertions.assertThrows(IllegalArgumentException.class, () -> authService.createUser(userCopy));
    }

    @Test
    void createUserExistsEmail() {
        UserSecurityDto user = new UserSecurityDto();
        user.setId("USER_ID");
        user.setUsername("TESTUSER");
        user.setEmail("some@email.com");
        user.setPassword("123456");
        authService.createUser(user);
        Assertions.assertEquals(1, userRepository.findAll().size());

        UserSecurityDto userCopy = new UserSecurityDto();
        userCopy.setId("USER_ID2");
        userCopy.setUsername("TESTUSER2");
        userCopy.setEmail("some@email.com");
        userCopy.setPassword("123456");
        Assertions.assertThrows(IllegalArgumentException.class, () -> authService.createUser(userCopy));
    }
}