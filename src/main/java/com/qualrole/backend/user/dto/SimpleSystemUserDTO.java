package com.qualrole.backend.user.dto;

import com.qualrole.backend.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * DTO para encapsular os dados de um usuario simples.
 * Inclui informações pessoais e contato básico.
 */
public record SimpleSystemUserDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(regexp = "\\d{10,15}", message = "Telefone inválido. Deve ter entre 10 e 15 caracteres e conter apenas números.")
        String phoneNumber,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        String cpfCnpj,
        AddressDTO addresses,
        LocalDate birthDate,
        Role role
) {

    public record AddressDTO(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    ) {
    }
}