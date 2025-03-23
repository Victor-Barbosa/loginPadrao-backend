package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.service.OAuth2UserRegistrationService;
import com.qualrole.backend.user.dto.SetPasswordRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pelo registro de usuários através de provedores OAuth2.
 */
@RestController
@RequestMapping("/api/users/oauth2")
public class OAuth2UserRegistrationController {

    private final OAuth2UserRegistrationService oAuth2UserRegistrationService;

    public OAuth2UserRegistrationController(OAuth2UserRegistrationService oAuth2UserRegistrationService) {
        this.oAuth2UserRegistrationService = oAuth2UserRegistrationService;
    }

    /**
     * Endpoint para definir ou atualizar a senha do usuário registrado via OAuth2.
     *
     * @param setPasswordRequestDTO DTO contendo o email e a nova senha.
     * @return Confirmação de sucesso.
     */
    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(@Valid @RequestBody SetPasswordRequestDTO setPasswordRequestDTO) {
        oAuth2UserRegistrationService.setPasswordForOAuth2User(setPasswordRequestDTO);
        return ResponseEntity.ok("Senha definida com sucesso!");
    }
}