package com.qualrole.backend.auth.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    /**
     * Gera um Refresh ‘Token’ ‘Cookie’ conforme os padrões de segurança.
     *
     * @param refreshToken Token que será armazenado no cookie.
     * @return Um {@link ResponseCookie} configurado.
     */
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 24 * 60 * 60)
                .build();
    }
}