package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.UpdatePasswordDTO;
import com.qualrole.backend.user.service.PasswordUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável por gerir atualizações de informações e senha do usuario logado.
 */
@RestController
@RequestMapping("/api/users")
public class SystemUserUpdatePasswordController {

    private final PasswordUpdateService passwordUpdateService;

    public SystemUserUpdatePasswordController(PasswordUpdateService passwordUpdateService) {
        this.passwordUpdateService = passwordUpdateService;
    }

    /**
     * Endpoint para atualizar a senha do próprio usuario.
     *
     * @param updatePasswordDTO Objeto com a senha atual e a nova senha.
     * @return Resposta de sucesso se a senha for atualizada.
     */
    @PutMapping("/update-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        passwordUpdateService.updatePassword(authenticatedUserId, updatePasswordDTO);
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }
}