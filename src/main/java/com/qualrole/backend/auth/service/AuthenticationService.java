package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.security.CookieUtil;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.UnauthorizedException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.exception.InvalidPasswordException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AuthenticationService {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(TokenService tokenService, CookieUtil cookieUtil,
                                 SystemUserRepository systemUserRepository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.cookieUtil = cookieUtil;
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Realiza o ‘login’ (autenticação do usuario) e gera os ‘Tokens’.
     *
     * @param email    E-mail do usuario.
     * @param password Senha do usuario.
     * @return Map contendo o novo Access Token e um Cookie com o Refresh Token.
     */
    public Map<String, Object> login(String email, String password) {
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o e-mail " + email + " não encontrado."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Senha inválida para o usuário " + email + ".");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user.getSystemUserId());
        log.info("refreshToken: {}", refreshToken);

        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(refreshToken);
        log.info("refreshCookie: {}", refreshCookie);

        return Map.of(
                "accessToken", accessToken,
                "refreshCookie", refreshCookie
        );
    }

    /**
     * Atualiza o Access Token e Refresh Token baseando-se no cookie do refresh token.
     *
     * @param refreshToken Cookie do Refresh Token.
     * @return Map contendo o novo Access ‘Token’ e um ‘Cookie’ atualizado com o novo Refresh ‘Token’.
     */
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new UnauthorizedException("Refresh token não encontrado no cookie.");
        }

        String userId = tokenService.validateAndExtractUserIdFromRefreshToken(refreshToken);

        tokenService.addRefreshTokenToBlacklist(refreshToken);

        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado ao validar o refresh token."));

        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user.getSystemUserId());

        ResponseCookie newRefreshCookie = cookieUtil.createRefreshTokenCookie(newRefreshToken);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshCookie", newRefreshCookie
        );
    }
}