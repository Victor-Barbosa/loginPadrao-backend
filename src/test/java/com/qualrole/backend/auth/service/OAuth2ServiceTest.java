package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.exception.OAuth2EmailNotFoundException;
import com.qualrole.backend.auth.security.CookieUtil;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OAuth2ServiceTest {

    private TokenService tokenService;
    private CookieUtil cookieUtil;
    private SystemUserRepository systemUserRepository;
    private OAuth2Service oAuth2Service;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        cookieUtil = mock(CookieUtil.class);
        systemUserRepository = mock(SystemUserRepository.class);
        oAuth2Service = new OAuth2Service(tokenService, cookieUtil, systemUserRepository);
    }

    @Test
    void testHandleOAuth2LoginSuccess_SuccessfulCase() {
        String email = "user@example.com";
        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "user-id");

        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).build();

        when(systemUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(user)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken("user-id")).thenReturn(refreshToken);
        when(cookieUtil.createRefreshTokenCookie(refreshToken)).thenReturn(refreshCookie);

        Map<String, Object> result = oAuth2Service.handleOAuth2LoginSuccess(email);

        assertEquals(accessToken, result.get("accessToken"));
        assertEquals("Login via OAuth2 realizado com sucesso!", result.get("message"));
        assertEquals(refreshCookie, result.get("refreshCookie"));
        verify(systemUserRepository).findByEmail(email);
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken("user-id");
        verify(cookieUtil).createRefreshTokenCookie(refreshToken);
    }

    @Test
    void testHandleOAuth2LoginSuccess_EmailNotProvided() {

        assertThrows(OAuth2EmailNotFoundException.class, () ->
                oAuth2Service.handleOAuth2LoginSuccess(null));
    }

    @Test
    void testHandleOAuth2LoginSuccess_UserNotFound() {
        String email = "nonexistent@example.com";

        when(systemUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        OAuth2EmailNotFoundException exception = assertThrows(OAuth2EmailNotFoundException.class, () ->
                oAuth2Service.handleOAuth2LoginSuccess(email));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(systemUserRepository).findByEmail(email);
    }

    @Test
    void testHandleOAuth2LoginFailure() {
        String errorMessage = oAuth2Service.handleOAuth2LoginFailure();

        assertEquals("O login via OAuth2 falhou. Tente novamente mais tarde.", errorMessage);
    }
}