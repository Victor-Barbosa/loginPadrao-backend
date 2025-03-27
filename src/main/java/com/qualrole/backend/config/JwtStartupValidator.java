package com.qualrole.backend.config;

import com.qualrole.backend.exception.InvalidJwtSecretKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Base64;

@Component
public class JwtStartupValidator {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @PostConstruct
    public void validateJwtSecret() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            if (keyBytes.length < 64) {
                throw new InvalidJwtSecretKeyException("A chave fornecida em 'app.jwt.secret' é inválida. " +
                        "Deve ter pelo menos 512 bits (64 caracteres codificados em base64).");
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtSecretKeyException("A chave para JWT no 'app.jwt.secret' " +
                    "não está codificada corretamente em base64.");
        }
    }
}