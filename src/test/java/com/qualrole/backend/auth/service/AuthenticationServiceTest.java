package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.security.CookieUtil;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.InvalidPasswordException;
import com.qualrole.backend.user.exception.UnauthorizedException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private TokenService tokenService;
    private CookieUtil cookieUtil;
    private SystemUserRepository systemUserRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        cookieUtil = mock(CookieUtil.class);
        systemUserRepository = mock(SystemUserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authenticationService = new AuthenticationService(
                tokenService, cookieUtil, systemUserRepository, passwordEncoder);
    }

    @Test
    void testLogin_Success() {
        String email = "user@example.com";
        String rawPassword = "password";
        String encodedPassword = "$2a$10$EiKIQxNAl7Ul4UuiBl9XKu0lBZR4Xa2NU/9IEFjwnnseMytUpQKta";
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", "12345");
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.STANDARD_USER);

        when(systemUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(user.getSystemUserId())).thenReturn(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).build();
        when(cookieUtil.createRefreshTokenCookie(refreshToken)).thenReturn(refreshCookie);

        var result = authenticationService.login(email, rawPassword);

        assertNotNull(result);
        assertEquals(accessToken, result.get("accessToken"));
        assertEquals(refreshCookie, result.get("refreshCookie"));

        verify(systemUserRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(user.getSystemUserId());
        verify(cookieUtil).createRefreshTokenCookie(refreshToken);
    }

    @Test
    void testLogin_InvalidPassword() {
        String email = "user@example.com";
        String rawPassword = "wrong-password";
        String encodedPassword = "$2a$10$EiKIQxNAl7Ul4UuiBl9XKu0lBZR4Xa2NU/9IEFjwnnseMytUpQKta";

        SystemUser user = new SystemUser();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(systemUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                authenticationService.login(email, rawPassword));

        assertEquals("Senha inválida para o usuário " + email + ".", exception.getMessage());

        verify(systemUserRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(cookieUtil);
    }

    @Test
    void testLogin_UserNotFound() {
        String email = "user@example.com";
        String password = "password";

        when(systemUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authenticationService.login(email, password));

        assertEquals("Usuário com o e-mail " + email + " não encontrado.", exception.getMessage());

        verify(systemUserRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(cookieUtil);
    }

    @Test
    void testRefreshAccessToken_Success() {
        String refreshToken = "valid-refresh-token";
        String userId = "1234";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        SystemUser user = new SystemUser();
        ReflectionTestUtils.setField(user, "systemUserId", userId);
        user.setEmail("user@example.com");
        user.setRole(Role.STANDARD_USER);

        when(tokenService.validateAndExtractUserIdFromRefreshToken(refreshToken)).thenReturn(userId);
        when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(user)).thenReturn(newAccessToken);
        when(tokenService.generateRefreshToken(userId)).thenReturn(newRefreshToken);

        ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newRefreshToken).build();
        when(cookieUtil.createRefreshTokenCookie(newRefreshToken)).thenReturn(newRefreshCookie);

        var result = authenticationService.refreshAccessToken(refreshToken);

        assertNotNull(result);
        assertEquals(newAccessToken, result.get("accessToken"));
        assertEquals(newRefreshCookie, result.get("refreshCookie"));

        verify(tokenService).validateAndExtractUserIdFromRefreshToken(refreshToken);
        verify(tokenService).addRefreshTokenToBlacklist(refreshToken);
        verify(systemUserRepository).findById(userId);
        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(userId);
        verify(cookieUtil).createRefreshTokenCookie(newRefreshToken);
    }

    @Test
    void testRefreshAccessToken_TokenNotFound() {
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                authenticationService.refreshAccessToken(null));

        assertEquals("Refresh token não encontrado no cookie.", exception.getMessage());
    }

    @Test
    void testRefreshAccessToken_UserNotFound() {
        String refreshToken = "valid-refresh-token";
        String userId = "1234";

        when(tokenService.validateAndExtractUserIdFromRefreshToken(refreshToken)).thenReturn(userId);
        when(systemUserRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authenticationService.refreshAccessToken(refreshToken));

        assertEquals("Usuário não encontrado ao validar o refresh token.", exception.getMessage());

        verify(tokenService).validateAndExtractUserIdFromRefreshToken(refreshToken);

        verify(tokenService).addRefreshTokenToBlacklist(refreshToken);

        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());

        verifyNoInteractions(cookieUtil);
    }
}