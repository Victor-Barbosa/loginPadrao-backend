package com.qualrole.backend.auth.exception;

/**
 * Exceção definida para indicar erros de acesso não autorizado.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
