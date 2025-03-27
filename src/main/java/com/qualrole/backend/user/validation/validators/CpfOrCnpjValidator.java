package com.qualrole.backend.user.validation.validators;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.qualrole.backend.user.validation.annotations.CpfOrCnpj;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador para a anotação {@link CpfOrCnpj}.
 * <p>
 * Realiza a validação de CPF ou CNPJ utilizando a biblioteca Stella.
 * </p>
 *
 * <b>Regras de Validação:</b>
 * <ul>
 *   <li>CPF deve conter 11 números.</li>
 *   <li>CNPJ deve conter 14 números.</li>
 *   <li>Caracteres não numéricos são ignorados automaticamente.</li>
 * </ul>
 *
 * <b>Exemplo de CPF válido:</b> `012.345.678-90`
 * <b>Exemplo de CNPJ válido:</b> `12.345.678/0001-00`
 */
public class CpfOrCnpjValidator implements ConstraintValidator<CpfOrCnpj, String> {

    /**
     * Valida se o valor informado é um CPF ou CNPJ válido.
     *
     * @param value   Valor a ser validado. Pode ser um CPF ou CNPJ.
     * @param context Contexto da validação.
     * @return {@code true} se o valor for válido, {@code false} caso contrário.
     */
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