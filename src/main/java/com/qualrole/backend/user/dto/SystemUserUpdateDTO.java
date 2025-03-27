package com.qualrole.backend.user.dto;

import java.time.LocalDate;


/**
 * DTO para encapsular os dados de um usuario do sistema durante o processo de atualização.
 * Inclui informações pessoais, de contato e endereço.
 */
public record SystemUserUpdateDTO(
        String name,
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