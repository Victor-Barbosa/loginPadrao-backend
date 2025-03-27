package com.qualrole.backend.user.exception;

/**
 * Exceção lançada quando campos obrigatórios não são fornecidos.
 */
public class MissingRequiredFieldsException extends RuntimeException {

    public MissingRequiredFieldsException(String message) {
        super(message);
    }
}