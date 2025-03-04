package com.usermanager.manager.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
    Long id,

    @NotBlank(message = "Name can not be blank")
    String name, 

    @NotBlank(message = "Login can not be blank")
    String login, 

    @NotBlank(message = "Password can not be blank")
    String password) {
}
