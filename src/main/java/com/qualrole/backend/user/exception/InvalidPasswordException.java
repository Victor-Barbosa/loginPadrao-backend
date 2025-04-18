package com.qualrole.backend.user.exception;

/**
 * Exceção lançada quando a senha fornecida não atende aos critérios definidos.
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}