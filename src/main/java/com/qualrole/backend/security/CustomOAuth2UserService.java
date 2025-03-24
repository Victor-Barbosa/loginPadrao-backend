package com.qualrole.backend.security;

import com.qualrole.backend.exception.OAuth2EmailNotFoundException;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.service.OAuth2SystemUserRegistrationService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Serviço customizado para lidar com usuarios vindos dos provedores OAuth2
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2SystemUserRegistrationService oAuth2SystemUserRegistrationService;
    private final SystemUserRepository systemUserRepository;

    public CustomOAuth2UserService(OAuth2SystemUserRegistrationService oAuth2SystemUserRegistrationService,
                                   SystemUserRepository systemUserRepository) {
        this.oAuth2SystemUserRegistrationService = oAuth2SystemUserRegistrationService;
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2EmailNotFoundException("Informações de email não encontradas no provedor OAuth2.");
        }

        SystemUser user = systemUserRepository.findByEmail(email).orElseGet(() ->
                oAuth2SystemUserRegistrationService.registerOAuth2User(email, name)
        );

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                Map.of(
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole().name()
                ),
                "email"
        );
    }
}