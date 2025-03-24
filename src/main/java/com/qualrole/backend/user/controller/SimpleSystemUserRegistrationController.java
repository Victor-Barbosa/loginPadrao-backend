package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.service.SimpleSystemUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável pelo registro de usuarios simples no sistema.
 */
@RestController
@RequestMapping("/api/users/simple")
public class SimpleSystemUserRegistrationController {

    private final SimpleSystemUserService simpleSystemUserService;

    public SimpleSystemUserRegistrationController(SimpleSystemUserService simpleSystemUserService) {
        this.simpleSystemUserService = simpleSystemUserService;
    }

    @PostMapping
    public ResponseEntity<String> registerSimpleUser(@Valid @RequestBody SimpleSystemUserDTO simpleSystemUserDTO) {
        simpleSystemUserService.registerSimpleSystemUser(simpleSystemUserDTO);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }
}