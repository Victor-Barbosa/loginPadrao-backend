package com.qualrole.backend.user.dto;

import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.validation.annotations.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O CPF/CNPJ é obrigatório.")
        @CpfCnpj
        String cpfCnpj,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "O telefone é obrigatório.")
        String telefone,

        @NotBlank(message = "O endereço é obrigatório.")
        String endereco,

        @NotNull(message = "A data de nascimento é obrigatória.")
        LocalDate dataNascimento,

        Role role
) {
}