package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuth2SuccessControllerTest {

    private JwtUtil jwtUtil;
    private OAuth2SuccessController oAuth2SuccessController;

    @BeforeEach
    void setUp() {
        jwtUtil = Mockito.mock(JwtUtil.class);
        oAuth2SuccessController = new OAuth2SuccessController(jwtUtil);
    }

    @Test
    void testSuccess() {
        String email = "testuser@example.com";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        when(jwtUtil.generateAccessToken(Map.of("role", "GUEST"), email)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(email)).thenReturn(refreshToken);

        ResponseEntity<Map<String, String>> response = oAuth2SuccessController.success(authentication);

        assertNotNull(response.getBody(), "Body da resposta não deve ser nulo");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login via OAuth2 realizado com sucesso!", response.getBody().get("message"));
        assertEquals(accessToken, response.getBody().get("token"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "Cabeçalhos devem conter 'Set-Cookie'");

        verify(jwtUtil).generateAccessToken(Map.of("role", "GUEST"), email);
        verify(jwtUtil).generateRefreshToken(email);
    }

    @Test
    void testFailure() {
        ResponseEntity<String> response = oAuth2SuccessController.failure();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O login via OAuth2 falhou. Tente novamente mais tarde.", response.getBody());
    }
}