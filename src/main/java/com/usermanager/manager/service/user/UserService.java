package com.usermanager.manager.service.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usermanager.manager.dto.user.UserDTO;
import com.usermanager.manager.dto.user.UserResponseDTO;
import com.usermanager.manager.exception.UserExistsException;
import com.usermanager.manager.exception.UserNotFoundException;
import com.usermanager.manager.infra.mail.MailService;
import com.usermanager.manager.mappers.UserMapper;
import com.usermanager.manager.model.user.User;
import com.usermanager.manager.model.verification.VerificationToken;
import com.usermanager.manager.repository.UserRepository;
import com.usermanager.manager.service.auth.VerificationTokenService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationService;
    private final MailService mailService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            VerificationTokenService verificationService, MailService mailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.mailService = mailService;
    }

    @Transactional
    public UserDTO createUser(@NotNull @Valid UserDTO dto) {
        if (userRepository.findByLogin(dto.login()).isPresent()) {
            throw new UserExistsException(dto.login());
        }

        User user = userMapper.userDTOToUser(dto);
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        user = userRepository.save(user);

        VerificationToken verificationToken = verificationService.generateVerificationToken(user);

        mailService.sendVerificationMail(user.getLogin(), verificationToken.getUuid().toString());

        return userMapper.userToUserDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(@NotNull @Valid UserResponseDTO dto) {
        User savedUser = (User) userRepository.findByLogin(dto.login()).orElseThrow(
                () -> new UserNotFoundException("with login: " + dto.login()));

        savedUser.setName(dto.name());
        savedUser.setLogin(dto.login());
        savedUser.setPassword(passwordEncoder.encode(dto.password()));

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
                () -> new UserNotFoundException("with ID: " + id));

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
