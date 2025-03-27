package com.qualrole.backend.user.excpetion;

/**
 * Exceção lançada quando o e-mail fornecido já está em uso no sistema.
 */
public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}