package com.usermanager.manager.mappers;

import org.mapstruct.Mapper;

import com.usermanager.manager.dto.UpdateUserDTO;
import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO dto);
    UpdateUserDTO userToUpdateUserDTO(User user);
}
