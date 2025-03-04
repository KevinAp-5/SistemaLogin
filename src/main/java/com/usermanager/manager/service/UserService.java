package com.usermanager.manager.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.usermanager.manager.dto.UpdateUserDTO;
import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.exception.UserExistsException;
import com.usermanager.manager.mappers.UserMapper;
import com.usermanager.manager.model.User;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO createUser(@NotNull @Valid UserDTO dto) {
        if (userRepository.findByLogin(dto.login()).isPresent()) {
            throw new UserExistsException(dto.login());
        }

        User user = userMapper.userDTOToUser(dto);
        user = userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }
}
