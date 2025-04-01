package com.qualrole.backend.config;

import com.qualrole.backend.exception.InvalidJwtSecretKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtStartupValidatorTest {

    private JwtStartupValidator jwtStartupValidator;

    @BeforeEach
    void setUp() {
        jwtStartupValidator = new JwtStartupValidator();
    }

    @Test
    void shouldValidateJwtSecretSuccessfullyWhenKeyIsValid() {
        String validKey = Base64.getEncoder().encodeToString(new byte[64]);
        ReflectionTestUtils.setField(jwtStartupValidator, "secretKey", validKey);

        assertDoesNotThrow(jwtStartupValidator::validateJwtSecret);
    }

    @Test
    void shouldThrowExceptionWhenKeyIsInvalidBase64() {
        String invalidBase64Key = "INVALID_BASE64_KEY";
        ReflectionTestUtils.setField(jwtStartupValidator, "secretKey", invalidBase64Key);

        assertThrows(InvalidJwtSecretKeyException.class, jwtStartupValidator::validateJwtSecret);
    }

    @Test
    void shouldThrowExceptionWhenKeyIsTooShort() {
        String shortKey = Base64.getEncoder().encodeToString(new byte[32]);
        ReflectionTestUtils.setField(jwtStartupValidator, "secretKey", shortKey);

        assertThrows(InvalidJwtSecretKeyException.class, jwtStartupValidator::validateJwtSecret);
    }

    @Test
    void shouldThrowExceptionWhenKeyIsEmpty() {
        ReflectionTestUtils.setField(jwtStartupValidator, "secretKey", "");

        assertThrows(InvalidJwtSecretKeyException.class, jwtStartupValidator::validateJwtSecret);
    }
}