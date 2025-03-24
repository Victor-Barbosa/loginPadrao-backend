package com.qualrole.backend.user.dto;

import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.validation.annotations.CpfOrCnpj;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * DTO para encapsular os dados de um usuário completo.
 * Inclui informações pessoais, contato e endereço.
 */
public record CompleteSystemUserDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O CPF/CNPJ é obrigatório.")
        @CpfOrCnpj
        String cpfCnpj,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(regexp = "\\d{10,15}", message = "Telefone inválido. Deve ter entre 10 e 25 caracteres " +
                "e conter apenas números, espaços e símbolos permitidos.")
        String phoneNumber,

        @NotNull(message = "O endereço é obrigatório.")
        @Valid
        AddressDTO addresses,

        @NotNull(message = "A data de nascimento é obrigatória.")
        LocalDate birthDate,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        Role role
) {

    public record AddressDTO(
            @NotBlank(message = "A rua é obrigatória.")
            String street,

            @NotBlank(message = "O número é obrigatório.")
            String number,

            String complement,

            @NotBlank(message = "O bairro é obrigatório.")
            String neighborhood,

            @NotBlank(message = "A cidade é obrigatória.")
            String city,

            @NotBlank(message = "O estado é obrigatório.")
            String state,

            @NotBlank(message = "O CEP é obrigatório.")
            String zipCode
    ) {
    }
}