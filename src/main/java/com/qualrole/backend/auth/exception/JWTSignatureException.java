package com.qualrole.backend.auth.exception;

/**
 * Exceção definida para erros de assinatura inválida do JWT.
 */
public class JWTSignatureException extends RuntimeException {

    public JWTSignatureException(String message) {
        super(message);
    }
}