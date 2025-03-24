package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por gerir o registro de usuarios completos no sistema.
 */
@Service
public class CompleteSystemUserService {

    private final SystemUserRepository systemUserRepository;
    private final UserValidator userValidator;
    private final PasswordValidator passwordValidator;
    private final UserBuilder userBuilder;
    private final PasswordEncoder passwordEncoder;

    public CompleteSystemUserService(SystemUserRepository systemUserRepository, UserValidator userValidator,
                                     PasswordValidator passwordValidator, UserBuilder userBuilder,
                                     PasswordEncoder passwordEncoder) {
        this.systemUserRepository = systemUserRepository;
        this.userValidator = userValidator;
        this.passwordValidator = passwordValidator;
        this.userBuilder = userBuilder;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerCompleteSystemUser(CompleteSystemUserDTO completeSystemUserDTO) {
        userValidator.validateCpfCnpjUniqueness(completeSystemUserDTO.cpfCnpj(), null);
        userValidator.validateEmailUniqueness(completeSystemUserDTO.email(), null);
        passwordValidator.validatePassword(completeSystemUserDTO.password());

        SystemUser user = userBuilder.buildNewCompleteSystemUser(completeSystemUserDTO);
        user.setPassword(passwordEncoder.encode(completeSystemUserDTO.password()));
        systemUserRepository.save(user);
    }
}