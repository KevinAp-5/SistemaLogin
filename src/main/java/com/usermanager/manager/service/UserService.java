package com.usermanager.manager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usermanager.manager.dto.UserDTO;
import com.usermanager.manager.dto.UserResponseDTO;
import com.usermanager.manager.exception.UserDoesNotExistException;
import com.usermanager.manager.exception.UserExistsException;
import com.usermanager.manager.mappers.UserMapper;
import com.usermanager.manager.model.User;
import com.usermanager.manager.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDTO createUser(@NotNull @Valid UserDTO dto) {
        if (userRepository.findByLogin(dto.login()).isPresent()) {
            throw new UserExistsException(dto.login());
        }

        User user = userMapper.userDTOToUser(dto);
        user = userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(@NotNull @Valid UserResponseDTO dto) {
        User savedUser = userRepository.findByLogin(dto.login()).orElseThrow(
            () -> new UserDoesNotExistException("with login: " + dto.login())
        );

        savedUser.setName(dto.name());
        savedUser.setLogin(dto.login());
        savedUser.setPassword(dto.password());

        User updatedUser = userRepository.save(savedUser);
        return userMapper.userToUserResponseDTO(updatedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .toList();
    }

    public UserDTO findUserById(@Positive @NotNull Long id) {
        User response = userRepository.findById(id).orElseThrow(
            () -> new UserDoesNotExistException("with ID: " + id)
        );

        return userMapper.userToUserDTO(response);
    }

    @Transactional
    public boolean deleteUserById(@Positive @NotNull Long id) {
        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            return false;
        }

        userRepository.delete(userToDelete);
        return true;
    }
}
