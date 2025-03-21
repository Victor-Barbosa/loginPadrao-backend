package com.qualrole.backend.validation.annotations;

import com.qualrole.backend.validation.validators.CpfCnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CpfCnpjValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CpfCnpj {

    String message() default "O documento informado não é um CPF ou CNPJ válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}