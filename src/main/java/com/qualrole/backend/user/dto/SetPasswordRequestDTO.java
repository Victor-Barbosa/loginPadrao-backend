package com.qualrole.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para a solicitação de definição de senha de usuários registrados via OAuth2.
 */
public record SetPasswordRequestDTO(
        @Email
        @NotBlank(message = "O e-mail é obrigatório.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String password
) {}