package com.qualrole.backend.user.exception;

/**
 * Exceção definida para indicar erros de acesso não autorizado.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
