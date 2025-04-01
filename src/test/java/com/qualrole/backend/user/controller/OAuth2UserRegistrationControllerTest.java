package com.qualrole.backend.user.controller;

import com.qualrole.backend.auth.exception.UnauthorizedException;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.service.OAuth2SystemUserService;
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

class OAuth2UserRegistrationControllerTest {

    @Mock
    private OAuth2SystemUserService oAuth2SystemUserService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private OAuth2UserRegistrationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new OAuth2UserRegistrationController(oAuth2SystemUserService);
    }

    @Test
    void shouldCompleteRegistrationSuccessfully() {
        // Arrange
        SimpleSystemUserDTO dto = new SimpleSystemUserDTO(
                "Test User",
                "test@example.com",
                "1234567890",
                "password",
                null,
                null,
                null,
                null
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(oAuth2SystemUserService).promoteGuestToStandardUser(any(SimpleSystemUserDTO.class));

        // Act
        ResponseEntity<String> response = controller.completeRegistration(dto);

        // Assert
        assertEquals("Cadastro atualizado!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(oAuth2SystemUserService, times(1)).promoteGuestToStandardUser(dto);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserIsNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext(); // Garante que não há contexto de autenticação

        SimpleSystemUserDTO dto = new SimpleSystemUserDTO(
                "Test User",
                "test@example.com",
                "1234567890",
                "password",
                null,
                null,
                null,
                null
        );

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                controller.completeRegistration(dto)
        );

        assertEquals("Usuário não está autenticado.", exception.getMessage());
        verify(oAuth2SystemUserService, never()).promoteGuestToStandardUser(any(SimpleSystemUserDTO.class));
    }
}