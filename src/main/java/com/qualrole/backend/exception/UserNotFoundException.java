package com.qualrole.backend.exception;

import jakarta.persistence.EntityNotFoundException;

/**
 * Disparada quando um usuario não é encontrado no banco de dados.
 */
public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}