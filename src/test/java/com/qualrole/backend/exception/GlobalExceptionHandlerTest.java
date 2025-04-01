package com.qualrole.backend.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithValidationErrors() throws NoSuchMethodException {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        FieldError fieldError1 = new FieldError(
                "TestObject", "field1", "Field1 is invalid");
        FieldError fieldError2 = new FieldError(
                "TestObject", "field2", "Field2 is required");

        Mockito.when(bindingResultMock.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodParameter methodParameterMock =
                new MethodParameter(this.getClass()
                        .getDeclaredMethod(
                                "handleValidationExceptions_ShouldReturnBadRequestWithValidationErrors"),
                        -1);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameterMock, bindingResultMock);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().error()).isEqualTo(
                "field1: Field1 is invalid, field2: Field2 is required");
        assertThat(response.getBody().timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}