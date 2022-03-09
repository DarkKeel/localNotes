package com.localnotes.service;

import com.localnotes.dto.AuthenticationRequestDto;
import com.localnotes.dto.UserDto;
import com.localnotes.entity.Role;
import com.localnotes.entity.Status;
import com.localnotes.entity.User;
import com.localnotes.mapper.UserMapper;
import com.localnotes.repository.RoleRepository;
import com.localnotes.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User register(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username: " + user.getUsername() + " already exists");
        } else if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email: " + user.getEmail() + " already exists");
        }
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        user.setPublicId(IdService.createUuid());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        user.setCreated(new Date());
        user.setUpdated(new Date());
        User registeredUser = userRepository.save(user);

        return registeredUser;
    }

    public List<User> getAll() {
        List<User> result = new ArrayList<>(userRepository.findAll());
        return result;
    }

    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username).orElse(null);
        return result;
    }

    public UserDto findByPublicId(String publicId) {
        User result = userRepository.findByPublicId(publicId).orElseThrow(() ->
                new EntityNotFoundException("UserService: findByPublicId: user with id: "
                        + publicId + " is not found"));
        if (result == null) {
            throw new EntityNotFoundException("User with id: " + publicId + " is not found");
        }
        return userMapper.toUserDto(result);
    }

    public void delete(String id) {
        User existsUser = userRepository.findByPublicId(id).orElseThrow(() ->
                new EntityNotFoundException("UserService: delete: user with id: " + id
                        + "doesn't exists"));

        userRepository.deleteById(existsUser.getId());
    }

    public void changePassword(String id, AuthenticationRequestDto requestDto) {
        User user = userRepository.findByPublicId(id).orElseThrow(() ->
                new EntityNotFoundException("UserService: changePassword: there is no user with id: " + id));
        if (!user.getUsername().equals(requestDto.getUsername())) {
            throw new IllegalArgumentException("Wrong data");
        }
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
    }

    public UserDto updateUser(String id, UserDto user) {
        User userEntity = userRepository.findByPublicId(id).orElseThrow(() ->
                new EntityNotFoundException("UserService: changePassword: there is no user with id: " + id));
        if (userRepository.findDuplicateUserByEmail(user.getId(), user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is in use");
        }
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        return userMapper.toUserDto(userRepository.save(userEntity));
    }
}
