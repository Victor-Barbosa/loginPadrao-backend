package com.qualrole.backend.auth.exception;

/**
 * Disparada quando o e-mail de um usuario não é encontrado no retorno do provedor OAuth2.
 */
public class OAuth2EmailNotFoundException extends RuntimeException {

    public OAuth2EmailNotFoundException(String message) {
        super(message);
    }
}