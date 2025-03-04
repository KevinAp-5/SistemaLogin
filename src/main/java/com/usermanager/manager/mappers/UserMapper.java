package com.usermanager.manager.mappers;

import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class UserMapper {

    private UserMapper() {}

    public static UserDTO toDTO(@NotNull User user) {
        return new UserDTO(user.getId(), user.getName(), user.getLogin(), user.getPassword());
    }

    public static User toModel(@Valid UserDTO dto) {
        return new User(dto.id(), dto.name(), dto.login(), dto.password());
    }
}
