package com.qualrole.backend.user.validation.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CpfOrCnpjValidatorTest {

    @Test
    void testIsValid_NullInput_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_EmptyInput_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid("", context));
    }

    @Test
    void testIsValid_BlankInput_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid("   ", context));
    }

    @Test
    void testIsValid_InvalidLength_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid("123456", context));
    }

    @Test
    void testIsValid_InvalidCPF_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid("12345678901", context));
    }

    @Test
    void testIsValid_ValidCPFWithoutFormatting_ReturnsTrue() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(validator.isValid("12345678909", context));
    }

    @Test
    void testIsValid_ValidCPFWithFormatting_ReturnsTrue() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(validator.isValid("123.456.789-09", context));
    }

    @Test
    void testIsValid_InvalidCNPJ_ReturnsFalse() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertFalse(validator.isValid("12345678000199", context));
    }

    @Test
    void testIsValid_ValidCNPJWithoutFormatting_ReturnsTrue() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(validator.isValid("12345678000195", context));
    }

    @Test
    void testIsValid_ValidCNPJWithFormatting_ReturnsTrue() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(validator.isValid("12.345.678/0001-95", context));
    }

    @Test
    void testIsValid_InputWithNonDigitCharacters_ReturnsValidResult() {
        CpfOrCnpjValidator validator = new CpfOrCnpjValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertTrue(validator.isValid("123.456.789-09", context));
        assertFalse(validator.isValid("123.456.789-00", context));
        assertTrue(validator.isValid("12.345.678/0001-95", context));
    }
}