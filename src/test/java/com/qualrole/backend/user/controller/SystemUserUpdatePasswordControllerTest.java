package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.UpdatePasswordDTO;
import com.qualrole.backend.user.service.PasswordUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SystemUserUpdatePasswordControllerTest {

    @Mock
    private PasswordUpdateService passwordUpdateService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private SystemUserUpdatePasswordController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new SystemUserUpdatePasswordController(passwordUpdateService);
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {

        String authenticatedUserId = "12345";
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("currentPassword", "newPassword");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUserId);
        SecurityContextHolder.setContext(securityContext);
        doNothing().when(passwordUpdateService).updatePassword(authenticatedUserId, updatePasswordDTO);

        ResponseEntity<String> response = controller.updatePassword(updatePasswordDTO);

        assertEquals("Senha atualizada com sucesso!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(passwordUpdateService).updatePassword(authenticatedUserId, updatePasswordDTO);
    }

    @Test
    void shouldThrowExceptionIfPasswordUpdateFails() {
        String authenticatedUserId = "12345";
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("currentPassword", "newPassword");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUserId);
        SecurityContextHolder.setContext(securityContext);
        doThrow(new RuntimeException("Password update failed")).when(passwordUpdateService).updatePassword(authenticatedUserId, updatePasswordDTO);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                controller.updatePassword(updatePasswordDTO)
        );

        assertEquals("Password update failed", exception.getMessage());
        verify(passwordUpdateService).updatePassword(authenticatedUserId, updatePasswordDTO);
    }
}