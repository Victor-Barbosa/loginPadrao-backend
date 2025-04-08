package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.exception.OAuth2EmailNotFoundException;
import com.qualrole.backend.auth.service.OAuth2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuth2SuccessControllerTest {

    private OAuth2Service oAuth2Service;
    private OAuth2SuccessController oAuth2SuccessController;

    @BeforeEach
    void setUp() {
        oAuth2Service = mock(OAuth2Service.class);
        oAuth2SuccessController = new OAuth2SuccessController(oAuth2Service);
    }

    @Test
    void testSuccess_SuccessfulLogin() {
        String email = "user@example.com";
        String accessToken = "access-token";
        String message = "Login via OAuth2 realizado com sucesso!";
        String refreshCookieValue = "refreshToken=refresh-token";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        Map<String, Object> serviceResponse = Map.of(
                "accessToken", accessToken,
                "message", message,
                "refreshCookie", refreshCookieValue
        );
        when(oAuth2Service.handleOAuth2LoginSuccess(email)).thenReturn(serviceResponse);

        ResponseEntity<Map<String, String>> response = oAuth2SuccessController.success(authentication);

        assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody().get("message"));
        assertEquals(accessToken, response.getBody().get("token"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "O cabeçalho deve conter 'Set-Cookie'");
        assertEquals(refreshCookieValue, response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));

        verify(authentication).getName();
        verify(oAuth2Service).handleOAuth2LoginSuccess(email);
    }

    @Test
    void testSuccess_UserNotFound() {
        String email = "nonexistent@example.com";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        when(oAuth2Service.handleOAuth2LoginSuccess(email))
                .thenThrow(new OAuth2EmailNotFoundException("Usuário com o e-mail " + email + " não encontrado."));

        OAuth2EmailNotFoundException exception = assertThrows(OAuth2EmailNotFoundException.class, () ->
                oAuth2SuccessController.success(authentication));
        assertEquals("Usuário com o e-mail " + email + " não encontrado.", exception.getMessage());

        verify(authentication).getName();
        verify(oAuth2Service).handleOAuth2LoginSuccess(email);
    }

    @Test
    void testFailure() {
        String errorMessage = "O login via OAuth2 falhou. Tente novamente mais tarde.";

        when(oAuth2Service.handleOAuth2LoginFailure()).thenReturn(errorMessage);

        ResponseEntity<String> response = oAuth2SuccessController.failure();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());

        verify(oAuth2Service).handleOAuth2LoginFailure();
    }
}