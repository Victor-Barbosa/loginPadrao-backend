package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.UserDTO;
import com.qualrole.backend.user.entity.User;
import com.qualrole.backend.user.repository.UserRepository;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserBuilder userBuilder;

    public UserService(UserRepository userRepository, UserValidator userValidator, UserBuilder userBuilder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userBuilder = userBuilder;
    }

    public void createUser(UserDTO userDTO) {
        userValidator.validateCpfCnpjUniqueness(userDTO.cpfCnpj(), null);
        userValidator.validateEmailUniqueness(userDTO.email(), null);

        User user = userBuilder.buildNewUser(userDTO);
        userRepository.save(user);
    }

    public User updateUser(String userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        userValidator.validateCpfCnpjUniqueness(userDTO.cpfCnpj(), userId);
        userValidator.validateEmailUniqueness(userDTO.email(), userId);

        existingUser = userBuilder.updateUser(existingUser, userDTO);
        return userRepository.save(existingUser);
    }
}