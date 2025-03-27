package com.qualrole.backend.user.exception;

/**
 * Exceção lançada quando um usuario não é encontrado no repositório.
 */
public class GuestUserNotFoundException extends RuntimeException {

    public GuestUserNotFoundException(String message) {
        super(message);
    }
}