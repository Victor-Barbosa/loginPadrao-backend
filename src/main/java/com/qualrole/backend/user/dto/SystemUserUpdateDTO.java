package com.qualrole.backend.user.dto;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;


/**
 * DTO para encapsular os dados de um usuario do sistema durante o processo de atualização.
 * Inclui informações pessoais, de contato e endereço.
 */
public record SystemUserUpdateDTO(
        String name,
        @Pattern(regexp = "\\d{10,15}", message = "Telefone inválido. Deve ter entre 10 e 25 caracteres " +
                "e conter apenas números, espaços e símbolos permitidos.")
        String phoneNumber,
        LocalDate birthDate,
        AddressDTO address
) {

    public record AddressDTO(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    ) {}
}