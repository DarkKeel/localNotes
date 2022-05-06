package com.localnotes.service;

import com.localnotes.dto.AuthenticationRequestDto;
import com.localnotes.dto.UserDto;
import com.localnotes.entity.User;
import com.localnotes.mapper.UserMapper;
import com.localnotes.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                       UserService userService, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Map<String, String> login(AuthenticationRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
        log.info("AuthService: login: findByUsername - username: {}", requestDto.getUsername());
        String username = requestDto.getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User with username: " + username + " not found");
        }
        String token = jwtTokenProvider.createToken(username, user.getRoles());
        Map<String, String> response = new HashMap<>();
        response.put("id", user.getPublicId());
        response.put("token", token);
        return response;
    }

    public UserDto createUser(User user) {
        log.info("AuthService: createUser: user with username: {} is creating.", user.getUsername());
        User newUser = userService.register(user);
        return userMapper.toUserDto(newUser);
    }
}
