package com.qualrole.backend.user.validation;

import com.qualrole.backend.user.exception.InvalidPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordValidatorTest {

    @Autowired
    private PasswordValidator passwordValidator;

    @Test
    public void whenPasswordIsValid_thenNoExceptionThrown() {
        String password = "Valid1@password";

        Assertions.assertDoesNotThrow(() -> passwordValidator.validatePassword(password));
    }

    @Test
    public void whenPasswordIsNull_thenThrowsInvalidPasswordException() {
        String password = null;

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected an InvalidPasswordException when password is null");
    }

    @Test
    public void whenPasswordIsTooShort_thenThrowsInvalidPasswordException() {
        String password = "Abc1@";

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected an InvalidPasswordException for password shorter than 8 characters");
    }

    @Test
    public void whenPasswordDoesNotContainUppercase_thenThrowsInvalidPasswordException() {
        String password = "lowercase1!";

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected InvalidPasswordException for missing uppercase letters");
    }

    @Test
    public void whenPasswordDoesNotContainLowercase_thenThrowsInvalidPasswordException() {
        String password = "UPPERCASE1!";

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected InvalidPasswordException for missing lowercase letters");
    }

    @Test
    public void whenPasswordDoesNotContainDigit_thenThrowsInvalidPasswordException() {
        String password = "Password!";

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected InvalidPasswordException for missing digits");
    }

    @Test
    public void whenPasswordDoesNotContainSpecialCharacter_thenThrowsInvalidPasswordException() {
        String password = "Password1";

        Assertions.assertThrows(InvalidPasswordException.class,
                () -> passwordValidator.validatePassword(password),
                "Expected InvalidPasswordException for missing special characters");
    }
}