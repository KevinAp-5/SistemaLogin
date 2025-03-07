package com.usermanager.manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.usermanager.manager.dto.AuthenticationDTO;
import com.usermanager.manager.dto.ResponseMessage;
import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.service.AuthService;
import com.usermanager.manager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService,
            UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO dto) {
        UserDTO response = userService.createUser(dto);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/api/users")
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri())
                .body(response);
    }

    @PostMapping("login")
    public ResponseEntity<ResponseMessage> login(@RequestBody @Valid AuthenticationDTO data) {
        if (authService.login(data))
            return ResponseEntity.ok().body(new ResponseMessage("User logged."));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Invalid credentials"));
    }

}
