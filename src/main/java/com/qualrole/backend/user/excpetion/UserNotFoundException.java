package com.qualrole.backend.user.excpetion;

import jakarta.persistence.EntityNotFoundException;

/**
 * Disparada quando um usuario não é encontrado no banco de dados.
 */
public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}