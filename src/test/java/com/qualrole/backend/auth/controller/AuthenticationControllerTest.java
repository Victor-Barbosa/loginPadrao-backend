package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.security.JwtUtil;
import com.qualrole.backend.auth.security.RedisTokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private JwtUtil jwtUtil;
    private RedisTokenBlacklistService tokenBlacklistService;
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        jwtUtil = Mockito.mock(JwtUtil.class);
        tokenBlacklistService = Mockito.mock(RedisTokenBlacklistService.class);
        authenticationController = new AuthenticationController(jwtUtil, tokenBlacklistService);
    }

    @Test
    void testLogin_Success() {
        String email = "test@example.com";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(jwtUtil.generateAccessToken(Map.of(), email)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(email)).thenReturn(refreshToken);

        AuthenticationController.LoginRequest loginRequest = new AuthenticationController
                .LoginRequest(email, "password");

        ResponseEntity<Map<String, String>> response = authenticationController.login(loginRequest);

        assertNotNull(response.getBody(), "Body da resposta não deve ser nulo");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accessToken, response.getBody().get("accessToken"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "Cabeçalhos devem conter SET_COOKIE");

        verify(jwtUtil).generateAccessToken(Map.of(), email);
        verify(jwtUtil).generateRefreshToken(email);
    }

    @Test
    void testRefreshToken_Success() {
        String refreshToken = "valid-refresh-token";
        String username = "tester";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.generateAccessToken(Map.of(), username)).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(username)).thenReturn(newRefreshToken);

        ResponseEntity<Map<String, String>> response = authenticationController.refreshToken(refreshToken);

        assertNotNull(response.getBody(), "Body da resposta não deve ser nulo");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newAccessToken, response.getBody().get("accessToken"));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE),
                "Cabeçalhos devem conter SET_COOKIE");

        verify(tokenBlacklistService).isTokenBlacklisted(refreshToken);
        verify(tokenBlacklistService).addTokenToBlacklist(refreshToken, 2 * 24 * 60 * 60 * 1000L);
        verify(jwtUtil).generateAccessToken(Map.of(), username);
        verify(jwtUtil).generateRefreshToken(username);
    }

    @Test
    void testRefreshToken_BlacklistedToken() {
        String refreshToken = "blacklisted-token";

        when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authenticationController.refreshToken(refreshToken);

        assertNotNull(response.getBody(), "Body da resposta não deve ser nulo");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token inválido ou revogado", response.getBody().get("error"));

        verify(tokenBlacklistService).isTokenBlacklisted(refreshToken);
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(jwtUtil, never()).generateAccessToken(Mockito.any(), anyString());
    }
}