package com.qualrole.backend.auth.controller;

import com.qualrole.backend.auth.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users/oauth2")
@Slf4j
public class OAuth2SuccessController {

    private final OAuth2Service oAuth2Service;

    public OAuth2SuccessController(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> success(Authentication authentication) {
        String email = authentication.getName();
        Map<String, Object> result = oAuth2Service.handleOAuth2LoginSuccess(email);

        String accessToken = (String) result.get("accessToken");
        String message = (String) result.get("message");
        var refreshCookie = result.get("refreshCookie").toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .body(Map.of(
                        "message", message,
                        "token", accessToken
                ));
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure() {
        String errorMessage = oAuth2Service.handleOAuth2LoginFailure();
        return ResponseEntity.badRequest().body(errorMessage);
    }
}