package com.qualrole.backend.user.validation;

import com.qualrole.backend.user.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por validar senhas com base em critérios predefinidos.
 */
@Component
public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+={}|<>\\[\\]~.-]).{8,}$";

    /**
     * Valida se a senha fornecida atende aos critérios estabelecidos.
     * Os critérios incluem:
     * <ul>
     *   <li>Mínimo de 8 caracteres.</li>
     *   <li>Pelo menos 1 número.</li>
     *   <li>Pelo menos 1 letra maiúscula.</li>
     *   <li>Pelo menos 1 letra minúscula.</li>
     *   <li>Pelo menos 1 caractere especial.</li>
     * </ul>
     *
     * @param password Senha fornecida pelo usuario.
     * @throws InvalidPasswordException Se a senha não atender aos critérios.
     */
    public void validatePassword(String password) {
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new InvalidPasswordException(
                    "A senha deve conter pelo menos 8 caracteres, com pelo menos 1 número, 1 letra maiúscula, " +
                            "1 letra minúscula e 1 caractere especial."
            );
        }
    }
}