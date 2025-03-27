package com.qualrole.backend.user.validation.annotations;

import com.qualrole.backend.user.validation.validators.CpfOrCnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação personalizada para validação de CPF ou CNPJ.
 * <p>
 * Utiliza o validador {@link CpfOrCnpjValidator}.
 * Pode ser aplicada a campos ou métodos.
 * </p>
 *
 * <b>Exemplo de Uso:</b>
 * <pre>{@code
 * @CpfOrCnpj
 * private String documento;
 * }</pre>
 */
@Constraint(validatedBy = CpfOrCnpjValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CpfOrCnpj {

    String message() default "O documento informado não é um CPF ou CNPJ válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}