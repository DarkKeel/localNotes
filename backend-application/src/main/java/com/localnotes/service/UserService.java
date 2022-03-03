package com.localnotes.service;

import com.localnotes.model.Role;
import com.localnotes.model.Status;
import com.localnotes.model.User;
import com.localnotes.repository.RoleRepository;
import com.localnotes.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);

        User registeredUser = userRepository.save(user);

        log.info("Register - user with id: {} successfully registered", registeredUser.getPublicId());

        return registeredUser;
    }

    public List<User> getAll() {
        List<User> result = new ArrayList<>(userRepository.findAll());
        return result;
    }

    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("FindByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("FindById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result);
        return result;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted");
    }



}
