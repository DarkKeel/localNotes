package com.localnotes.security;

import com.localnotes.entity.User;
import com.localnotes.security.jwt.JwtUser;
import com.localnotes.security.jwt.JwtUserFactory;
import com.localnotes.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        JwtUser jwtUser = JwtUserFactory.create(user);

        log.debug("JwtUserDetailsService: loadUserByUsername: user with username: {} successfully loaded", username);

        return jwtUser;
    }
}
