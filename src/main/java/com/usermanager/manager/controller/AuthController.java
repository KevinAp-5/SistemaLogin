package com.usermanager.manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.usermanager.manager.dto.authentication.ActivateUserDTO;
import com.usermanager.manager.dto.authentication.AuthenticationDTO;
import com.usermanager.manager.dto.authentication.CreateUserDTO;
import com.usermanager.manager.dto.authentication.LoginResponseDTO;
import com.usermanager.manager.dto.authentication.PasswordResetDTO;
import com.usermanager.manager.dto.authentication.UserCreatedDTO;
import com.usermanager.manager.dto.authentication.UserEmailDTO;
import com.usermanager.manager.dto.common.ResponseMessage;
import com.usermanager.manager.service.auth.AuthService;
import com.usermanager.manager.service.auth.VerificationTokenService;
import com.usermanager.manager.service.user.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth/")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final VerificationTokenService verificationService;

    public AuthController(AuthService authService, UserService userService,
            VerificationTokenService verificationService) {
        this.authService = authService;
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @PostMapping("register")
    public ResponseEntity<UserCreatedDTO> createUser(@RequestBody @Valid CreateUserDTO dto) {
        UserCreatedDTO response = userService.createUser(dto);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/api/users")
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri())
                .body(response);
    }

    @GetMapping("register/confirm")
    public ResponseEntity<ResponseMessage> confirmUser(@RequestParam("token") @NotBlank String token) {
        boolean validated = verificationService.confirmVerificationToken(token);
        if (validated) {
            return ResponseEntity.ok(new ResponseMessage("User confirmed with success."));
        }
        return ResponseEntity.badRequest().body(new ResponseMessage("Unable to activate user. Try again later"));
    }

    @PostMapping("password/forget")
    public ResponseEntity<ResponseMessage> sendPasswordResetCode(@RequestBody @Valid UserEmailDTO data) {
        boolean response = authService.sendPasswordResetCode(data);
        if (!response)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("User is not enabled. please confirm the email."));

        return ResponseEntity.ok(new ResponseMessage("Password reset link was sent to your e-mail."));
    }

    @PostMapping("password/reset")
    public ResponseEntity<ResponseMessage> confirmPasswordReset(@RequestParam @NotBlank String token,
    @RequestBody @Valid PasswordResetDTO data) {
        authService.passwordReset(token, data);

        return ResponseEntity.ok().body(new ResponseMessage("Password changed successfully."));
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var token = authService.login(data);
        if (token != null)
            return ResponseEntity.ok().body(new LoginResponseDTO(token));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("activate")
    public ResponseEntity<ResponseMessage> activateUser(@RequestBody @Valid ActivateUserDTO data) {
        boolean activationSent = authService.activateUser(data.email());
        if (!activationSent)
            return ResponseEntity.status(200)
                    .body(new ResponseMessage("User is already active with email: " + data.email()));

        return ResponseEntity.ok(new ResponseMessage("Activation link sent to " + data.email() + " successfully."));
    }
}