package com.qualrole.backend.user.builder;

import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.dto.CompleteSystemUserDTO.AddressDTO;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.AddressUser;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por construir objetos de usuários do sistema
 * baseados em DTOs (Data Transfer Objects).
 */
@Component
public class UserBuilder {

    /**
     * Constrói um novo usuário completo do sistema a partir de um DTO.
     *
     * @param completeSystemUserDTO DTO contendo os dados completos do usuário.
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
     * Constrói um novo usuário simples do sistema a partir de um DTO.
     *
     * @param simpleSystemUserDTO DTO contendo os dados do usuário simples.
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
                Role.USER
        );
    }

    /**
     * Mapeia um DTO de endereço para a entidade de endereço.
     *
     * @param dto DTO representando o endereço.
     * @return {@link AddressUser} com as informações do endereço.
     */
    private AddressUser mapToAddressEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        return new AddressUser(
                null,
                dto.street(),
                dto.number(),
                dto.complement(),
                dto.neighborhood(),
                dto.city(),
                dto.state(),
                dto.zipCode(),
                null
        );
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
}