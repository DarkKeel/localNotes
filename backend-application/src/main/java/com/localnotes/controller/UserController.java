package com.localnotes.controller;

import com.localnotes.dto.AuthenticationRequestDto;
import com.localnotes.dto.UserDto;
import com.localnotes.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getInfo(@PathVariable String id) {
        return userService.findByPublicId(id);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity changePassword(@PathVariable String id, @RequestBody AuthenticationRequestDto requestDto) {
        userService.changePassword(id, requestDto);
        return ResponseEntity.ok("Password has been changed");
    }

    @PutMapping("/{id}/info")
    public UserDto changeInfo(@PathVariable String id, @RequestBody UserDto user) {
        return userService.updateUser(id, user);
    }
}
