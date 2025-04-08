package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.service.AuthenticationService;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.exception.InvalidPasswordException;
import com.qualrole.backend.user.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private AuthenticationService authenticationService;
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        authenticationController = new AuthenticationController(authenticationService);
    }


    @Test
    void shouldReturnAccessTokenOnLoginSuccess() {
        String email = "test@example.com";
        String password = "password";
        String accessToken = "test-access-token";
        String refreshCookieValue = "refreshToken=test-refresh-token";

        AuthenticationController.LoginRequest loginRequest =
                new AuthenticationController.LoginRequest(email, password);

        Map<String, Object> serviceResponse = Map.of(
                "accessToken", accessToken,
                "refreshCookie", refreshCookieValue
        );
        when(authenticationService.login(email, password)).thenReturn(serviceResponse);

        ResponseEntity<Map<String, String>> response = authenticationController.login(loginRequest);

        assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo.");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accessToken, response.getBody().get("accessToken"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "O cabeçalho deve conter 'Set-Cookie'.");
        assertEquals(refreshCookieValue, response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

        verify(authenticationService).login(email, password);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        String email = "nonexistent@example.com";
        String password = "password";

        when(authenticationService.login(email, password)).thenThrow(
                new UserNotFoundException("Usuário com o e-mail " + email + " não encontrado.")
        );

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authenticationService.login(email, password)
        );

        assertEquals("Usuário com o e-mail nonexistent@example.com não encontrado.", exception.getMessage());
        verify(authenticationService).login(email, password);
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() {
        String email = "test@example.com";
        String invalidPassword = "wrong-password";

        when(authenticationService.login(email, invalidPassword)).thenThrow(
                new InvalidPasswordException("Senha inválida para o usuário " + email + ".")
        );

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                authenticationService.login(email, invalidPassword)
        );

        assertEquals("Senha inválida para o usuário test@example.com.", exception.getMessage());
        verify(authenticationService).login(email, invalidPassword);
    }


    @Test
    void shouldReturnNewAccessTokenAndRefreshTokenOnRefreshSuccess() {
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshCookieValue = "refreshToken=new-refresh-token";

        Map<String, Object> serviceResponse = Map.of(
                "accessToken", newAccessToken,
                "refreshCookie", newRefreshCookieValue
        );
        when(authenticationService.refreshAccessToken(refreshToken)).thenReturn(serviceResponse);

        ResponseEntity<?> response = authenticationController.refreshToken(refreshToken);

        assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo.");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newAccessToken, ((Map<?, ?>) response.getBody()).get("accessToken"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "O cabeçalho deve conter 'Set-Cookie'.");
        assertEquals(newRefreshCookieValue, response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

        verify(authenticationService).refreshAccessToken(refreshToken);
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsNullOrEmpty() {

        when(authenticationService.refreshAccessToken(null)).thenThrow(
                new UnauthorizedException("Refresh token não encontrado no cookie.")
        );

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                authenticationService.refreshAccessToken(null)
        );

        assertEquals("Refresh token não encontrado no cookie.", exception.getMessage());
        verify(authenticationService).refreshAccessToken(null);
    }

    @Test
    void shouldReturnNotFoundWhenUserIsNotFoundForRefreshToken() {
        String refreshToken = "invalid-refresh-token";

        when(authenticationService.refreshAccessToken(refreshToken)).thenThrow(
                new UserNotFoundException("Usuário não encontrado ao validar o refresh token.")
        );

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authenticationService.refreshAccessToken(refreshToken)
        );

        assertEquals("Usuário não encontrado ao validar o refresh token.", exception.getMessage());
        verify(authenticationService).refreshAccessToken(refreshToken);
    }
}