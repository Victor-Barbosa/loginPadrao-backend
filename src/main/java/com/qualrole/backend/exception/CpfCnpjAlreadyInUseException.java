package com.qualrole.backend.exception;

public class CpfCnpjAlreadyInUseException extends RuntimeException {

    public CpfCnpjAlreadyInUseException(String message) {
        super(message);
    }
}