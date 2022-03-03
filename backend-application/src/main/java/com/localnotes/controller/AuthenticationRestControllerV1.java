package com.localnotes.controller;

import com.localnotes.dto.AuthenticationRequestDto;
import com.localnotes.dto.UserDto;
import com.localnotes.entity.User;
import com.localnotes.mapper.UserMapper;
import com.localnotes.security.jwt.JwtTokenProvider;
import com.localnotes.service.UserService;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthenticationRestControllerV1(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            UserMapper userMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("login")
    public Map<Object, Object> login(@RequestBody AuthenticationRequestDto requestDto) {
        String username = requestDto.getUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
        log.info("AuthController: login: findByUsername - username: {}", username);
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User with username: " + username + " not found");
        }
        String token = jwtTokenProvider.createToken(username, user.getRoles());
        Map<Object, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", token);
        return response;
    }

    @PostMapping("/create")
    public UserDto createUser(@RequestBody User user) {
        log.info("AuthController: createUser: user with username: " + user.getUsername() + " is creating.");
        User newUser = userService.register(user);
        return userMapper.toUserDto(newUser);
    }
}
