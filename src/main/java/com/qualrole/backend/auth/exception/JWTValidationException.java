package com.qualrole.backend.auth.exception;

/**
 * Exceção definida para erros gerais de validação do JWT.
 */
public class JWTValidationException extends RuntimeException {

    public JWTValidationException(String message) {
        super(message);
    }
}