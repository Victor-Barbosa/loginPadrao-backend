package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.service.OAuth2SystemUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável pelo registro e promoção de usuarios atraves de provedores OAuth2.
 */
@RestController
@RequestMapping("/api/users/oauth2")
public class OAuth2UserRegistrationController {

    private final OAuth2SystemUserService oAuth2SystemUserService;

    public OAuth2UserRegistrationController(OAuth2SystemUserService oAuth2SystemUserService) {
        this.oAuth2SystemUserService = oAuth2SystemUserService;
    }

    /**
     * Endpoint para promover um usuario GUEST para STANDARD_USER, completando o cadastro.
     *
     * @param simpleSystemUserDTO DTO contendo os dados do usuario.
     * @return Confirmação de sucesso.
     */
    @PostMapping("/complete")
    public ResponseEntity<String> completeRegistration(@Valid @RequestBody SimpleSystemUserDTO simpleSystemUserDTO) {
        oAuth2SystemUserService.promoteGuestToStandardUser(simpleSystemUserDTO);
        return ResponseEntity.ok("Cadastro atualizado!");
    }
}