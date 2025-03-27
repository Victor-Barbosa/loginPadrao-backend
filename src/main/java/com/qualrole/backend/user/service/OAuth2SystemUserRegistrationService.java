package com.qualrole.backend.user.service;

import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo registry de usuarios via OAuth2 e pela definição de senhas posteriormente.
 */
@Service
public class OAuth2SystemUserRegistrationService {

    private final SystemUserRepository systemUserRepository;
    private final UserValidator userValidator;

    public OAuth2SystemUserRegistrationService(SystemUserRepository systemUserRepository,
                                               UserValidator userValidator) {
        this.systemUserRepository = systemUserRepository;
        this.userValidator = userValidator;
    }

    /**
     * Registra um usuario com base nas informações vindas de um provedor OAuth2.
     *
     * @param email email do usuario.
     * @param name  Nome do usuario.
     */
    public SystemUser registerOAuth2User(String email, String name) {
        userValidator.validateEmailUniqueness(email, null);

        SystemUser newUser = new SystemUser();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPassword(null);
        newUser.setRole(Role.GUEST);
        return systemUserRepository.save(newUser);
    }
}