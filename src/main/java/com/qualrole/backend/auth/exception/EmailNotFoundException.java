package com.qualrole.backend.auth.exception;

/**
 * Exceção lançada quando o e-mail de um usuario não é encontrado no sistema.
 */
public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String message) {
        super(message);
    }
}