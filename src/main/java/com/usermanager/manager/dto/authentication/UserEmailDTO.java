package com.usermanager.manager.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailDTO(@Email @NotBlank String email) {

}
