package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por gerenciar o registro de usuários simples no sistema.
 */
@Service
public class SimpleSystemUserService {

    private final SystemUserRepository systemUserRepository;
    private final UserValidator userValidator;
    private final PasswordValidator passwordValidator;
    private final UserBuilder userBuilder;
    private final PasswordEncoder passwordEncoder;

    public SimpleSystemUserService(SystemUserRepository systemUserRepository, UserValidator userValidator,
                                   PasswordValidator passwordValidator, UserBuilder userBuilder,
                                   PasswordEncoder passwordEncoder) {
        this.systemUserRepository = systemUserRepository;
        this.userValidator = userValidator;
        this.passwordValidator = passwordValidator;
        this.userBuilder = userBuilder;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerSimpleSystemUser(SimpleSystemUserDTO simpleSystemUserDTO) {
        userValidator.validateCpfCnpjUniqueness(simpleSystemUserDTO.cpfCnpj(), null);
        userValidator.validateEmailUniqueness(simpleSystemUserDTO.email(), null);
        passwordValidator.validatePassword(simpleSystemUserDTO.password());

        SystemUser user = userBuilder.buildNewSimpleSystemUser(simpleSystemUserDTO);
        user.setPassword(passwordEncoder.encode(simpleSystemUserDTO.password()));
        systemUserRepository.save(user);
    }
}