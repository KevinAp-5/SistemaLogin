package com.usermanager.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usermanager.manager.dto.AuthenticationDTO;
import com.usermanager.manager.dto.ResponseMessage;
import com.usermanager.manager.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody @Valid AuthenticationDTO data) {
         authService.login(data, this.authenticationManager);
        return ResponseEntity.ok().body(new ResponseMessage("User logged."));
    }

}
