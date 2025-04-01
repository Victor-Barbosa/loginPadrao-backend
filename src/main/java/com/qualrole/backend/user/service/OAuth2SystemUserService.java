package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.GuestUserNotFoundException;
import com.qualrole.backend.user.exception.InvalidUserRoleException;
import com.qualrole.backend.user.exception.MissingRequiredFieldsException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por promover usuarios GUEST para STANDARD_USER.
 */
@Service
public class OAuth2SystemUserService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserBuilder userBuilder;

    public OAuth2SystemUserService(SystemUserRepository systemUserRepository,
                                   PasswordValidator passwordValidator,
                                   PasswordEncoder passwordEncoder,
                                   UserBuilder userBuilder) {
        this.systemUserRepository = systemUserRepository;
        this.passwordValidator = passwordValidator;
        this.passwordEncoder = passwordEncoder;
        this.userBuilder = userBuilder;
    }

    /**
     * Promove um usuario GUEST para STANDARD_USER, completando os dados do cadastro.
     *
     * @param simpleSystemUserDTO Dados do DTO fornecido pelo cliente.
     */
    @Transactional
    public void promoteGuestToStandardUser(SimpleSystemUserDTO simpleSystemUserDTO) {
        SystemUser existingUser = systemUserRepository.findByEmail(simpleSystemUserDTO.email())
                .orElseThrow(() -> new GuestUserNotFoundException("Usuário GUEST não encontrado."));

        validateGuestRole(existingUser);

        validateRequiredFields(simpleSystemUserDTO);

        userBuilder.updateOAuthSystemUser(existingUser, simpleSystemUserDTO);
        existingUser.setPassword(passwordEncoder.encode(simpleSystemUserDTO.password()));

        existingUser.setRole(Role.STANDARD_USER);

        systemUserRepository.save(existingUser);
    }

    /**
     * Valida se o usuario é GUEST.
     *
     * @param existingUser Usuario existente.
     */
    private void validateGuestRole(SystemUser existingUser) {
        if (!existingUser.getRole().equals(Role.GUEST)) {
            throw new InvalidUserRoleException("Somente usuários com a role GUEST podem ser promovidos.");
        }
    }

    /**
     * Valida os campos obrigatórios enviados no DTO.
     *
     * @param simpleSystemUserDTO Dados do DTO.
     */
    private void validateRequiredFields(SimpleSystemUserDTO simpleSystemUserDTO) {
        if (simpleSystemUserDTO.name() == null || simpleSystemUserDTO.name().isBlank()) {
            throw new MissingRequiredFieldsException("O nome é obrigatório para completar o cadastro.");
        }
        if (simpleSystemUserDTO.phoneNumber() == null || simpleSystemUserDTO.phoneNumber().isBlank()) {
            throw new MissingRequiredFieldsException("O telefone é obrigatório para completar o cadastro.");
        }
        if (simpleSystemUserDTO.password() == null || simpleSystemUserDTO.password().isBlank()) {
            throw new MissingRequiredFieldsException("A senha é obrigatória para completar o cadastro.");
        }
        passwordValidator.validatePassword(simpleSystemUserDTO.password());
    }
}