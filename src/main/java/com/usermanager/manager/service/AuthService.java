package com.usermanager.manager.service;


import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.usermanager.manager.dto.AuthenticationDTO;
import com.usermanager.manager.exception.UserNotEnabledException;
import com.usermanager.manager.exception.UserNotFoundException;
import com.usermanager.manager.model.security.TokenProvider;
import com.usermanager.manager.model.user.User;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, @Lazy AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userRepository.findByLogin(username)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials: verify login or password"));
        if (!user.isEnabled())
            throw new UserNotEnabledException(username);
        return user;
    }

    public String login(@Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        try {
            Authentication auth = authenticationManager.authenticate(usernamePassword);
            log.info("user {} sucessfully authenticated", data.login());
            return tokenProvider.generateToken((User) auth.getPrincipal());

        } catch (AuthenticationException e) {
            log.info("Authentication failed for user {user}: {}", data.login(), e.getMessage());
            throw new BadCredentialsException("Invalid credentials!");
        }
    }

}
