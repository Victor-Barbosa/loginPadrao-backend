package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.service.CompleteSystemUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pelo registro de usuários completos no sistema.
 */
@RestController
@RequestMapping("/api/users/complete")
public class CompleteSystemUserRegistrationController {

    private final CompleteSystemUserService completeSystemUserService;

    public CompleteSystemUserRegistrationController(CompleteSystemUserService completeSystemUserService) {
        this.completeSystemUserService = completeSystemUserService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody CompleteSystemUserDTO completeSystemUserDTO) {
        completeSystemUserService.registerCompleteSystemUser(completeSystemUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso!");
    }
}