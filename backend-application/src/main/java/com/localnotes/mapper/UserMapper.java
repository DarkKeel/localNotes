package com.localnotes.mapper;

import com.localnotes.dto.UserDto;
import com.localnotes.entity.User;
import com.localnotes.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User toUserEntity(UserDto dto) {
        User entity = userRepository.findByPublicId(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new User();
        }
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());

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
