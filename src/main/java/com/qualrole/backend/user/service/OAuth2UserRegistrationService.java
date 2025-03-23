package com.qualrole.backend.user.service;

import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.dto.SetPasswordRequestDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pelo registro de usuários via OAuth2 e pela definição de senhas posteriormente.
 */
@Service
public class OAuth2UserRegistrationService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final UserValidator userValidator;

    public OAuth2UserRegistrationService(SystemUserRepository systemUserRepository, PasswordEncoder passwordEncoder,
                                         PasswordValidator passwordValidator, UserValidator userValidator) {
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.userValidator = userValidator;
    }

    /**
     * Define a senha para um usuário previamente registrado via OAuth2.
     *
     * @param setPasswordRequestDTO DTO contendo o e-mail do usuário e a nova senha.
     */
    public void setPasswordForOAuth2User(SetPasswordRequestDTO setPasswordRequestDTO) {
        String email = setPasswordRequestDTO.email();
        String newPassword = setPasswordRequestDTO.password();

        passwordValidator.validatePassword(newPassword);

        Optional<SystemUser> existingUser = systemUserRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("Usuário não encontrado para o e-mail fornecido.");
        }

        SystemUser user = existingUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        systemUserRepository.save(user);
    }

    /**
     * Registra um usuário com base nas informações vindas de um provedor OAuth2.
     *
     * @param email E-mail do usuário.
     * @param name  Nome do usuário.
     */
    public void registerOAuth2User(String email, String name) {
        userValidator.validateEmailUniqueness(email, null);

        SystemUser newUser = new SystemUser();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPassword(null);
        newUser.setRole(Role.USER);
        systemUserRepository.save(newUser);
    }
}