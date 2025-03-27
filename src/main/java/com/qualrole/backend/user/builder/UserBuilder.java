package com.qualrole.backend.user.builder;

import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.dto.CompleteSystemUserDTO.AddressDTO;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.dto.SystemUserUpdateDTO;
import com.qualrole.backend.user.entity.AddressUser;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por construir objetos de usuarios do sistema
 * baseado em DTOs (Data Transfer Objects).
 */
@Component
public class UserBuilder {

    /**
     * Constrói um novo usuario completo do sistema a partir de um DTO.
     *
     * @param completeSystemUserDTO DTO contendo os dados completos do usuario.
     * @return {@link SystemUser} com informações completas.
     */
    public SystemUser buildNewCompleteSystemUser(CompleteSystemUserDTO completeSystemUserDTO) {
        AddressUser address = mapToAddressEntity(completeSystemUserDTO.addresses());
        return new SystemUser(
                null,
                completeSystemUserDTO.name(),
                completeSystemUserDTO.cpfCnpj(),
                completeSystemUserDTO.email(),
                completeSystemUserDTO.phoneNumber(),
                address,
                completeSystemUserDTO.birthDate(),
                completeSystemUserDTO.password(),
                Role.EVENT_CREATOR
        );
    }

    /**
     * Constrói um novo usuario simples do sistema a partir de um DTO.
     *
     * @param simpleSystemUserDTO DTO contendo os dados do usuario simples.
     * @return {@link SystemUser} com informações básicas.
     */
    public SystemUser buildNewSimpleSystemUser(SimpleSystemUserDTO simpleSystemUserDTO) {
        AddressUser address = simpleSystemUserDTO.addresses() != null
                ? mapToAddressEntity(mapSimpleAddressDTOToCompleteAddressDTO(simpleSystemUserDTO.addresses()))
                : null;

        return new SystemUser(
                null,
                simpleSystemUserDTO.name(),
                simpleSystemUserDTO.cpfCnpj(),
                simpleSystemUserDTO.email(),
                simpleSystemUserDTO.phoneNumber(),
                address,
                simpleSystemUserDTO.birthDate(),
                simpleSystemUserDTO.password(),
                Role.STANDARD_USER
        );
    }

    /**
     * Atualiza os campos de um usuario existente com base nos dados de um SimpleSystemUserDTO.
     *
     * @param existingUser        {@link SystemUser} usuario existente no sistema.
     * @param simpleSystemUserDTO {@link SimpleSystemUserDTO} Dados fornecidos para atualização.
     */
    public void updateOAuthSystemUser(SystemUser existingUser, SimpleSystemUserDTO simpleSystemUserDTO) {
        existingUser.setName(simpleSystemUserDTO.name());
        existingUser.setPhoneNumber(simpleSystemUserDTO.phoneNumber());

        if (simpleSystemUserDTO.cpfCnpj() != null && !simpleSystemUserDTO.cpfCnpj().isBlank()) {
            existingUser.setCpfCnpj(simpleSystemUserDTO.cpfCnpj());
        }

        if (simpleSystemUserDTO.birthDate() != null) {
            existingUser.setBirthDate(simpleSystemUserDTO.birthDate());
        }

        if (simpleSystemUserDTO.addresses() != null) {
            AddressUser updatedAddress = mapToAddressEntity(
                    mapSimpleAddressDTOToCompleteAddressDTO(simpleSystemUserDTO.addresses())
            );
            existingUser.setAddress(updatedAddress);
        }
    }

    /**
     * Atualiza os campos de um usuario existente com base nos dados de um SystemUserUpdateDTO.
     *
     * @param existingUser {@link SystemUser} usuario existente no sistema.
     * @param updateDTO    {@link SystemUserUpdateDTO} Dados fornecidos para atualização.
     */
    public void updateSystemUser(SystemUser existingUser, SystemUserUpdateDTO updateDTO) {
        if (updateDTO.name() != null) {
            existingUser.setName(updateDTO.name());
        }

        if (updateDTO.phoneNumber() != null) {
            existingUser.setPhoneNumber(updateDTO.phoneNumber());
        }

        if (updateDTO.birthDate() != null) {
            existingUser.setBirthDate(updateDTO.birthDate());
        }

        if (updateDTO.address() != null) {
            AddressUser updatedAddress = mapToAddressEntity(updateDTO.address());
            existingUser.setAddress(updatedAddress);
        }
    }

    /**
     * Mapeia um DTO de endereço para a entidade de endereço.
     *
     * @param dto DTO representando o endereço.
     * @return {@link AddressUser} com as informações do endereço.
     */
    private AddressUser mapToAddressEntity(AddressDTO dto) {
        if (isAddressEmpty(dto)) {
            return null;
        }
        return mapAddressToEntity(dto.street(), dto.number(), dto.complement(), dto.neighborhood(),
                dto.city(), dto.state(), dto.zipCode());
    }

    /**
     * Mapeia um DTO de endereço geral (endereços do tipo SystemUserUpdateDTO).
     *
     * @param dto DTO representando o endereço.
     * @return {@link AddressUser} com as informações do endereço.
     */
    private AddressUser mapToAddressEntity(SystemUserUpdateDTO.AddressDTO dto) {
        if (isAddressEmpty(dto)) {
            return null;
        }
        return mapAddressToEntity(dto.street(), dto.number(), dto.complement(), dto.neighborhood(),
                dto.city(), dto.state(), dto.zipCode());
    }

    /**
     * Converte um endereço simples (DTO) para um endereço completo (DTO).
     *
     * @param simpleAddressDTO Endereço simples no formato {@link SimpleSystemUserDTO.AddressDTO}.
     * @return Endereço completo no formato {@link CompleteSystemUserDTO.AddressDTO}.
     */
    private CompleteSystemUserDTO.AddressDTO mapSimpleAddressDTOToCompleteAddressDTO(
            SimpleSystemUserDTO.AddressDTO simpleAddressDTO) {
        return new CompleteSystemUserDTO.AddressDTO(
                simpleAddressDTO.street(),
                simpleAddressDTO.number(),
                simpleAddressDTO.complement(),
                simpleAddressDTO.neighborhood(),
                simpleAddressDTO.city(),
                simpleAddressDTO.state(),
                simpleAddressDTO.zipCode()
        );
    }

    /**
     * Verifica se um DTO de endereço está vazio (todos os campos nulos ou em branco).
     *
     * @param dto DTO de endereço.
     * @return {@code true} se o DTO estiver vazio, caso contrário {@code false}.
     */
    private boolean isAddressEmpty(Object dto) {
        return switch (dto) {
            case CompleteSystemUserDTO.AddressDTO completeAddress -> isFieldsEmpty(
                    completeAddress.street(),
                    completeAddress.number(),
                    completeAddress.complement(),
                    completeAddress.neighborhood(),
                    completeAddress.city(),
                    completeAddress.state(),
                    completeAddress.zipCode()
            );
            case SystemUserUpdateDTO.AddressDTO updateAddress -> isFieldsEmpty(
                    updateAddress.street(),
                    updateAddress.number(),
                    updateAddress.complement(),
                    updateAddress.neighborhood(),
                    updateAddress.city(),
                    updateAddress.state(),
                    updateAddress.zipCode()
            );
            default -> true;
        };
    }

    /**
     * Metodo auxiliar que verifica se todos os campos do endereço estão vazios ou nulos.
     *
     * @param fields Campos a serem verificados.
     * @return {@code true} se todos os campos forem vazios ou nulos, caso contrário {@code false}.
     */
    private boolean isFieldsEmpty(String... fields) {
        for (String field : fields) {
            if (field != null && !field.isBlank()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo auxiliar para criar uma entidade AddressUser.
     */
    private AddressUser mapAddressToEntity(String street, String number, String complement, String neighborhood,
                                           String city, String state, String zipCode) {
        return new AddressUser(
                null,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                null
        );
    }
}