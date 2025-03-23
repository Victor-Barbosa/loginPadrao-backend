package com.qualrole.backend.exception;

/**
 * Exceção lançada quando o CPF ou CNPJ fornecido já está em uso no sistema.
 */
public class CpfOrCnpjAlreadyInUseException extends RuntimeException {

    public CpfOrCnpjAlreadyInUseException(String message) {
        super(message);
    }
}