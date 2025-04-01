package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.security.RedisTokenBlacklistService;
import com.qualrole.backend.auth.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final RedisTokenBlacklistService tokenBlacklistService;

    public AuthenticationController(JwtUtil jwtUtil, RedisTokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.email();
        String accessToken = jwtUtil.generateAccessToken(Map.of(), username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido ou revogado"));
        }

        String username = jwtUtil.extractUsername(refreshToken);

        String accessToken = jwtUtil.generateAccessToken(Map.of(), username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        tokenBlacklistService.addTokenToBlacklist(refreshToken, 2 * 24 * 60 * 60 * 1000);

        ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body(Map.of(
                        "accessToken", accessToken
                ));
    }

    public record LoginRequest(String email, String password) {
    }
}