package com.qualrole.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para atualização de senha do usuario.
 */
public record UpdatePasswordDTO(
        @NotBlank(message = "A senha atual é obrigatória.") String currentPassword,
        @NotBlank(message = "A nova senha é obrigatória.") String newPassword
) {}