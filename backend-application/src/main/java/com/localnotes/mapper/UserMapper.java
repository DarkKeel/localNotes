package com.localnotes.mapper;

import com.localnotes.dto.UserSecurityDto;
import com.localnotes.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User toUserEntity(UserSecurityDto securityDto) {
        User entity = new User();
        entity.setEmail(securityDto.getEmail());
        entity.setFirstName(securityDto.getFirstName());
        entity.setLastName(securityDto.getLastName());
        entity.setUsername(securityDto.getUsername());
        entity.setPassword(securityDto.getPassword());

        return entity;
    }
}
