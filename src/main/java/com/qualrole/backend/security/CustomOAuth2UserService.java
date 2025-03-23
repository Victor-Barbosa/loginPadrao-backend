package com.qualrole.backend.security;

import com.qualrole.backend.exception.OAuth2EmailNotFoundException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.service.OAuth2UserRegistrationService;
import com.qualrole.backend.user.validation.UserValidator;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Serviço customizado para lidar com usuários vindos dos provedores OAuth2
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserRegistrationService oAuth2UserRegistrationService;
    private final SystemUserRepository systemUserRepository;
    private final UserValidator userValidator;

    public CustomOAuth2UserService(OAuth2UserRegistrationService oAuth2UserRegistrationService,
                                   SystemUserRepository systemUserRepository, UserValidator userValidator) {
        this.oAuth2UserRegistrationService = oAuth2UserRegistrationService;
        this.systemUserRepository = systemUserRepository;
        this.userValidator = userValidator;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2EmailNotFoundException("Informações de email não encontradas no provedor OAuth2.");
        }

        userValidator.validateEmailUniqueness(email, null);
        oAuth2UserRegistrationService.registerOAuth2User(email, name);

        SystemUser user = systemUserRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("Usuário não encontrado, mesmo após registro.")
        );

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of("email", user.getEmail(), "name", user.getName()),
                "email"
        );
    }
}