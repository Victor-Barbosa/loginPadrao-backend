package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = authenticationService.login(
                loginRequest.email(),
                loginRequest.password()
        );

        String accessToken = (String) result.get("accessToken");
        var refreshCookie = result.get("refreshCookie").toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .body(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        Map<String, Object> result = authenticationService.refreshAccessToken(refreshToken);

        String newAccessToken = (String) result.get("accessToken");
        var newRefreshCookie = result.get("refreshCookie").toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie)
                .body(Map.of("accessToken", newAccessToken));
    }

    public record LoginRequest(String email, String password) {}
}