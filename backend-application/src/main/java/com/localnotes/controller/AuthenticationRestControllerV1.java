package com.localnotes.controller;

import com.localnotes.dto.AuthenticationRequestDto;
import com.localnotes.dto.UserDto;
import com.localnotes.entity.User;
import com.localnotes.service.AuthService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthService authService;

    public AuthenticationRestControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public Map<String, String> login(@RequestBody AuthenticationRequestDto requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/create")
    public UserDto createUser(@RequestBody User user) {
        return authService.createUser(user);
    }
}
