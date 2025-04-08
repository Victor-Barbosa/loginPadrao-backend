package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.service.OAuth2SystemUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
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

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCompleteRegistrationSuccessfully() {
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

        String authenticatedUserId = "authenticated-user-id";
        when(authentication.getName()).thenReturn(authenticatedUserId);

        doNothing().when(oAuth2SystemUserService).promoteGuestToStandardUser(eq(authenticatedUserId), eq(dto));

        ResponseEntity<String> response = controller.completeRegistration(dto);

        assertEquals("Cadastro atualizado!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(oAuth2SystemUserService, times(1))
                .promoteGuestToStandardUser(authenticatedUserId, dto);
    }

    @Test
    void shouldThrowAccessDeniedWhenUserIsNotGuest() {
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

        String authenticatedUserId = "authenticated-user-id";
        when(authentication.getName()).thenReturn(authenticatedUserId);

        doThrow(new AccessDeniedException("Acesso negado"))
                .when(oAuth2SystemUserService).promoteGuestToStandardUser(eq(authenticatedUserId), eq(dto));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                controller.completeRegistration(dto)
        );

        assertEquals("Acesso negado", exception.getMessage());
        verify(oAuth2SystemUserService, times(1))
                .promoteGuestToStandardUser(authenticatedUserId, dto);
    }
}