package com.usermanager.manager.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.usermanager.manager.dto.AuthenticationDTO;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByLogin(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found with login: " + username)
        );
    }

    public void login(@Valid AuthenticationDTO data, AuthenticationManager authenticationManager) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        authenticationManager.authenticate(usernamePassword);        
    }
}
