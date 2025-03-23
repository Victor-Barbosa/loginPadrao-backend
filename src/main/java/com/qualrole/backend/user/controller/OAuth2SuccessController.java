package com.qualrole.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/oauth2")
public class OAuth2SuccessController {

    @GetMapping("/success")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("Login via OAuth2 realizado com sucesso!");
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure() {
        return ResponseEntity.badRequest().body("O login via OAuth2 falhou. Tente novamente mais tarde.");
    }
}