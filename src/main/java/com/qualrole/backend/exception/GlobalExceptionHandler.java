package com.qualrole.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que gerencia todas as exceções lançadas no sistema e retorna respostas padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CpfOrCnpjAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleCpfCnpjAlreadyInUse(CpfOrCnpjAlreadyInUseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(OAuth2EmailNotFoundException.class)
    public ResponseEntity<Object> handleOAuth2EmailNotFound(OAuth2EmailNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(String.format("%s: %s", fieldName, errorMessage));
        });

        String finalMessage = String.join(", ", validationErrors);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, finalMessage);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado: " + ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}