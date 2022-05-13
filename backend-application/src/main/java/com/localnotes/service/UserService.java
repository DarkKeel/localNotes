package com.localnotes.service;

import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.Role;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.mapper.UserMapper;
import com.localnotes.repository.RoleRepository;
import com.localnotes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(UserSecurityDto userDto) {
        validate(userDto);
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = Collections.singletonList(roleUser);

        User userEntity = userMapper.toUserEntity(userDto);
        userEntity.setPublicId(IdService.createUuid());
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRoles(userRoles);
        userEntity.setStatus(Status.ACTIVE);
        userEntity.setCreated(LocalDateTime.now());
        userEntity.setUpdated(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: " + username + " not found"));
    }

    private void validate(UserSecurityDto user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username: " + user.getUsername() + " already exists");
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email: " + user.getEmail() + " already exists");
        }
    }
}
