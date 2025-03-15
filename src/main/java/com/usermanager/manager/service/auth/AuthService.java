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
import com.usermanager.manager.infra.mail.MailService;
import com.usermanager.manager.model.security.TokenProvider;
import com.usermanager.manager.model.user.User;
import com.usermanager.manager.model.verification.enums.TokenType;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationService;
    private final MailService mailService;
    private AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, @Lazy AuthenticationManager authenticationManager,
            TokenProvider tokenProvider, PasswordEncoder passwordEncoder,
            VerificationTokenService verificationService, MailService mailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials: verify login or password"));

    }

    public String login(@Valid AuthenticationDTO data) {
        log.info("login attempt by {}", data.login());

        var user = (User) userRepository.findByLogin(data.login())
                .orElseThrow(() -> new UserNotFoundException("with login: " + data.login()));

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        if (!user.isEnabled()) {
            log.info("user {} not enabled. unable to login", data.login());
            throw new UserNotEnabledException("User not enabled. Please activate the email " + user.getLogin());
        }

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new BadCredentialsException("Bad cretentials: verify login or password.");
        }

        Authentication auth = authenticationManager.authenticate(usernamePassword);
        log.info("user {} sucessfully authenticated", data.login());
        return tokenProvider.generateToken((User) auth.getPrincipal());
    }

    public boolean activateUser(@Email @NotBlank String email) {
        User user = (User) userRepository.findByLogin(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isEnabled()) {
            return false;
        }

        var verificationToken = verificationService.generateVerificationToken(user, TokenType.EMAIL_VALIDATION);
        mailService.sendVerificationMail(user.getLogin(), verificationToken.getUuid().toString());
        return true;
    }
}
