package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SystemUserUpdateDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.service.SystemUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável por gerir a atualização de dados dos usuarios do sistema.
 */
@RestController
@RequestMapping("/api/users")
public class SystemUserUpdateController {

    private final SystemUserService systemUserService;

    public SystemUserUpdateController(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SystemUser> updateUser(@RequestBody SystemUserUpdateDTO updateDTO) {
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        SystemUser updatedUser = systemUserService.updateSystemUser(authenticatedUserId, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }
}