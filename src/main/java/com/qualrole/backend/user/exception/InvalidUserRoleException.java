package com.qualrole.backend.user.exception;

/**
 * Exceção lançada quando um usuario não tem a role GUEST.
 */
public class InvalidUserRoleException extends RuntimeException {

    public InvalidUserRoleException(String message) {
        super(message);
    }
}