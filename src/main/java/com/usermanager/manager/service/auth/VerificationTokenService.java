package com.usermanager.manager.service.auth;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usermanager.manager.exception.authentication.TokenInvalid;
import com.usermanager.manager.exception.authentication.TokenInvalidException;
import com.usermanager.manager.exception.authentication.TokenNotFoundException;
import com.usermanager.manager.model.user.User;
import com.usermanager.manager.model.verification.VerificationToken;
import com.usermanager.manager.model.verification.enums.TokenType;
import com.usermanager.manager.repository.UserRepository;
import com.usermanager.manager.repository.VerificationTokenRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationRepository;
    private final UserRepository userRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.verificationRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VerificationToken generateVerificationToken(@NotNull @Valid User user, TokenType tokenType) {
        UUID token = UUID.randomUUID();
        VerificationToken verificationToken = VerificationToken.builder()
            .uuid(token)
            .user(user)
            .creationDate(ZonedDateTime.now().toInstant())
            .expirationDate(ZonedDateTime.now().plusHours(24).toInstant())
            .tokenType(tokenType)
            .build();
        return verificationRepository.save(verificationToken);   
    }

    @Transactional
    public boolean confirmVerificationToken(@NotBlank String token) {
        // Getting the UUID from String prevents attacks like SQL INJECTION
        VerificationToken verificationToken = verificationRepository.findByUuid(UUID.fromString(token)).orElseThrow(
            () -> new TokenNotFoundException("Verification token was not found")
        );

        if (verificationToken.getExpirationDate().isBefore(ZonedDateTime.now().toInstant())) {
            throw new TokenInvalid("Token is expired, please try again");
        }

        // Enables user and saves it
        User user = verificationToken.getUser();
        user.setIsEnabled(true);
        userRepository.save(user);

        // confirms the activation of the verification token
        verificationToken.setActivationDate(ZonedDateTime.now().toInstant());
        verificationToken.setActivated(true);
        verificationRepository.save(verificationToken);

        return true;
    }

    public VerificationToken findVerificationByToken(@Valid @NotBlank String token) {
        UUID uuid = UUID.fromString(token);
        var verificationToken = verificationRepository.findByUuid(uuid)
                .orElseThrow(() -> new TokenNotFoundException("Verification token not found"));
        
        if (verificationToken.getExpirationDate().isBefore(ZonedDateTime.now().toInstant())) {
            throw new TokenInvalidException("Token is expired, please try again.");
        }

        return verificationToken;
    }

    @Transactional
    public void saveVerificationToken(VerificationToken verificationToken) {
        verificationRepository.save(verificationToken);
    }
}
