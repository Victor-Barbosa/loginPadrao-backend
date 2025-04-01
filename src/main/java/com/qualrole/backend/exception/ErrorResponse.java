package com.qualrole.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Representa a estrutura de uma resposta de erro ao cliente.
 */
public record ErrorResponse(int status, String error, LocalDateTime timestamp) {

    public ErrorResponse(HttpStatus status, String error) {
        this(status.value(), error, LocalDateTime.now());
    }
}