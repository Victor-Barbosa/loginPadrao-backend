package com.qualrole.backend.exception;

/**
 * Exceção lançada quando a configuração da chave JWT é inválida.
 */
public class InvalidJwtSecretKeyException extends RuntimeException {

    public InvalidJwtSecretKeyException(String message) {
        super(message);
    }
}