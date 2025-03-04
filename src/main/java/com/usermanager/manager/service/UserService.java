package com.usermanager.manager.service;

import org.springframework.stereotype.Service;

import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.mappers.UserMapper;
import com.usermanager.manager.model.User;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(@Valid UserDTO dto) {
        User user = UserMapper.toModel(dto);
        user = userRepository.save(user);
        return UserMapper.toDTO(user);
    }
}
