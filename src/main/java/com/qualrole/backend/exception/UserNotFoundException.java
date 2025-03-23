package com.qualrole.backend.exception;

/**
 * Disparada quando um usuário não é encontrado no banco de dados.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}