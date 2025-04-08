package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.security.JwtUtil;
import com.qualrole.backend.user.entity.SystemUser;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisTokenBlacklistService tokenBlacklistService;

    public TokenService(JwtUtil jwtUtil, RedisTokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Gera um accessToken com base nos dados do usuario.
     *
     * @param user Objeto SystemUser que contém as informações do usuario.
     * @return O ‘token’ de acesso gerado.
     */
    public String generateAccessToken(SystemUser user) {
        return jwtUtil.generateAccessToken(user);
    }

    /**
     * Gera um refreshToken com base no userId (usuario).
     *
     * @param userId UUID do usuario que será utilizado como Subject do ‘token’.
     * @return O ‘token’ de atualização gerado.
     */
    public String generateRefreshToken(String userId) {
        return jwtUtil.generateRefreshToken(userId);
    }

    public String validateAndExtractUserIdFromRefreshToken(String refreshToken) {
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new RuntimeException("O refresh token foi revogado.");
        }
        return jwtUtil.extractUserId(refreshToken);
    }

    public void addRefreshTokenToBlacklist(String refreshToken) {
        long expiration = jwtUtil.extractClaims(refreshToken).getExpiration().getTime() - System.currentTimeMillis();
        tokenBlacklistService.addTokenToBlacklist(refreshToken, expiration);
    }
}