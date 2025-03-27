package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users/oauth2")
public class OAuth2SuccessController {

    private final JwtUtil jwtUtil;

    public OAuth2SuccessController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> success(Authentication authentication) {

        String email = authentication.getName();

        String accessToken = jwtUtil.generateAccessToken(Map.of("role", "USER"), email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "message", "Login via OAuth2 realizado com sucesso!",
                        "token", accessToken
                ));
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure() {
        return ResponseEntity.badRequest().body("O login via OAuth2 falhou. Tente novamente mais tarde.");
    }
}