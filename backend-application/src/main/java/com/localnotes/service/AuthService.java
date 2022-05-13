package com.localnotes.service;

import com.localnotes.dto.AuthenticationRequest;
import com.localnotes.dto.AuthenticationResponse;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.User;
import com.localnotes.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthenticationResponse login(AuthenticationRequest requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
        log.info("AuthService: login: findByUsername - username: {}", requestDto.getUsername());
        String username = requestDto.getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User with username: " + username + " not found");
        }
        String token = jwtTokenProvider.createToken(username, user.getRoles());
        return new AuthenticationResponse(user.getPublicId(), token);
    }

    public void createUser(UserSecurityDto user) {
        log.info("AuthService: createUser: user with username: {} is creating.", user.getUsername());
        userService.register(user);
    }
}
