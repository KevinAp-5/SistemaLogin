package com.usermanager.manager.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.usermanager.manager.dto.UpdateUserDTO;
import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO dto, UriComponentsBuilder uri) {
        UserDTO userSavedDTO = userService.createUser(dto);
        URI userURI = buildURI(uri, userSavedDTO.id());
        return ResponseEntity.created(userURI).body(userSavedDTO);
    }

    @PutMapping("update")
    public ResponseEntity<UpdateUserDTO> updateUser(@RequestBody @Valid UpdateUserDTO dto) {
        var updatedUser = userService.updateUser(dto);
        return ResponseEntity.ok(updatedUser);
    }

    private URI buildURI(UriComponentsBuilder uri, Long id) {
        return uri.path("/api/users/" + id).buildAndExpand(id).toUri();
    }
}
