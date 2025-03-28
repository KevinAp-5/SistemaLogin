package com.usermanager.manager.service.auth;

import java.time.ZonedDateTime;
import java.util.UUID;

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
import org.springframework.transaction.annotation.Transactional;

import com.usermanager.manager.dto.authentication.AuthenticationDTO;
import com.usermanager.manager.dto.authentication.PasswordResetDTO;
import com.usermanager.manager.dto.authentication.UserEmailDTO;
import com.usermanager.manager.exception.user.UserNotEnabledException;
import com.usermanager.manager.infra.mail.MailService;
import com.usermanager.manager.model.security.TokenProvider;
import com.usermanager.manager.model.user.User;
import com.usermanager.manager.model.verification.enums.TokenType;
import com.usermanager.manager.service.user.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationService;
    private final MailService mailService;
    private AuthenticationManager authenticationManager;

    public AuthService(UserService userService, @Lazy AuthenticationManager authenticationManager,
            TokenProvider tokenProvider, PasswordEncoder passwordEncoder,
            VerificationTokenService verificationService, MailService mailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findUserByLoginOptional(username)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials: verify login or password"));

    }

    public String login(@Valid AuthenticationDTO data) {
        log.info("login attempt by {}", data.login());

        var user = userService.findUserByLogin(data.login());

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        if (!user.isEnabled()) {
            log.info("user {} not enabled. unable to login", data.login());
            throw new UserNotEnabledException("User not enabled. Please activate the email " + user.getLogin());
        }

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new BadCredentialsException("Bad credentials: verify login or password.");
        }

        Authentication auth = authenticationManager.authenticate(usernamePassword);
        log.info("user {} sucessfully authenticated", data.login());
        return tokenProvider.generateToken((User) auth.getPrincipal());
    }

    public boolean sendActivationCode(@Email @NotBlank String email) {
        User user = userService.findUserByLogin(email);

        if (user.isEnabled()) {
            return false;
        }

        var verificationToken = verificationService.generateVerificationToken(user, TokenType.EMAIL_VALIDATION);
        mailService.sendVerificationMail(user.getLogin(), verificationToken.getUuid().toString());
        return true;
    }

    @Transactional
    public boolean sendPasswordResetCode(@Valid UserEmailDTO data) {
        var user = userService.findUserByLogin(data.email());

        if (!user.isEnabled()) {
            return false;
        }

        var verificationToken = verificationService.generateVerificationToken(user, TokenType.RESET_PASSWORD);
        mailService.sendResetPasswordEmail(user.getLogin(), verificationToken.getUuid().toString());
        return true;
    }

    @Transactional
    public void passwordReset(@NotBlank UUID token, @Valid PasswordResetDTO data) {
        var verificationToken = verificationService.findVerificationByToken(token);
        User user = verificationToken.getUser();
        log.info("user {} has requested a password change.", user.getLogin());

        // Updates password and saves it
        user.setPassword(passwordEncoder.encode(data.newPassword()));
        userService.saveUser(user);
        log.info("user {} has changed password", user.getLogin());

        // Updates verificationToken to set it as activated/enabled
        verificationToken.setActivationDate(ZonedDateTime.now().toInstant());
        verificationToken.setActivated(true);
        verificationService.saveVerificationToken(verificationToken);
    }

}
