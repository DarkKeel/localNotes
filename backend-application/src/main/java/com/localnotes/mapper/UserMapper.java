package com.localnotes.mapper;

import com.localnotes.dto.UserDto;
import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.User;
import com.localnotes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public User toUserEntity(UserSecurityDto securityDto) {
        User entity = userRepository.findByUsername(securityDto.getUsername()).orElse(null);
        if (entity == null) {
            entity = new User();
        }
        entity.setEmail(securityDto.getEmail());
        entity.setFirstName(securityDto.getFirstName());
        entity.setLastName(securityDto.getLastName());
        entity.setUsername(securityDto.getUsername());
        entity.setPassword(securityDto.getPassword());

        return entity;
    }

    public UserDto toUserDto(User entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getPublicId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());

        return dto;
    }
}
