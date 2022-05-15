package com.localnotes.config.security;

import com.localnotes.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
public abstract class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    protected static final String LOGIN_ENDPOINT = "/api/v1/auth/**";

    protected final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
