package com.localnotes.controller;

import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.AuthenticationResponse;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.service.AuthService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@RequestBody UserSecurityDto user) {
        authService.createUser(user);
        return ResponseEntity.created(URI.create("")).build();
    }
}
