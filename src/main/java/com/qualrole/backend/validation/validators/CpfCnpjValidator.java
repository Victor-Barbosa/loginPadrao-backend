package com.qualrole.backend.validation.validators;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.qualrole.backend.validation.annotations.CpfCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        value = value.replaceAll("\\D", "");

        try {
            if (value.length() == 11) {
                new CPFValidator().assertValid(value);
                return true;
            }

            if (value.length() == 14) {
                new CNPJValidator().assertValid(value);
                return true;
            }
        } catch (InvalidStateException e) {
            return false;
        }

        return false;
    }
}