package com.usermanager.manager.service.auth;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.usermanager.manager.dto.authentication.AuthenticationDTO;
import com.usermanager.manager.exception.user.UserNotEnabledException;
import com.usermanager.manager.exception.user.UserNotFoundException;
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
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, @Lazy AuthenticationManager authenticationManager,
            TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
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
        log.info("login attempt by {}", data.login());
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        // Verify if the login provided exists in the database to proceed
        var user = (User) userRepository.findByLogin(data.login())
                .orElseThrow(() -> new UserNotFoundException("with login: " + data.login())
        );

        // Validates if the passwords matches to proceed with the login
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new BadCredentialsException("Bad cretentials: verify login or password.");
        }

        // At this stage, the passwords are equals and it will be possible to authenticate the user
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        log.info("user {} sucessfully authenticated", data.login());
        return tokenProvider.generateToken((User) auth.getPrincipal());
    }

}
