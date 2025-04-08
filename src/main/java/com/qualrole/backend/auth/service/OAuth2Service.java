package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.exception.OAuth2EmailNotFoundException;
import com.qualrole.backend.auth.security.CookieUtil;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class OAuth2Service {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final SystemUserRepository systemUserRepository;

    public OAuth2Service(TokenService tokenService, CookieUtil cookieUtil,
                         SystemUserRepository systemUserRepository) {
        this.tokenService = tokenService;
        this.cookieUtil = cookieUtil;
        this.systemUserRepository = systemUserRepository;
    }

    /**
     * Lida com o sucesso no login via OAuth2.
     *
     * @param email E-mail do usuario autenticado pelo provedor OAuth2.
     * @return Map contendo o Access ‘Token’, mensagem de sucesso e o ‘Cookie’ do Refresh ‘Token’ gerado.
     */
    public Map<String, Object> handleOAuth2LoginSuccess(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new OAuth2EmailNotFoundException(
                    "O e-mail do usuário não foi encontrado no retorno do provedor OAuth2.");
        }

        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2EmailNotFoundException("Usuário não encontrado"));

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user.getSystemUserId());
        log.info("refreshToken: {}", refreshToken);

        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(refreshToken);
        log.info("refreshCookie: {}", refreshCookie);

        return Map.of(
                "accessToken", accessToken,
                "message", "Login via OAuth2 realizado com sucesso!",
                "refreshCookie", refreshCookie
        );
    }

    /**
     * Lida com falhas no ‘login’ OAuth2.
     *
     * @return Mensagem de erro em formato de String.
     */
    public String handleOAuth2LoginFailure() {
        return "O login via OAuth2 falhou. Tente novamente mais tarde.";
    }
}