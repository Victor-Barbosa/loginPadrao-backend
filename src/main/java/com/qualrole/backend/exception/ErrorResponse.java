package com.qualrole.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final int status;
    private final String error;
    private final LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String error) {
        this.status = status.value();
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}