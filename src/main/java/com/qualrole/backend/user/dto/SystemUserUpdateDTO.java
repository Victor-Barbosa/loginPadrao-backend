package com.qualrole.backend.user.dto;

import java.time.LocalDate;

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