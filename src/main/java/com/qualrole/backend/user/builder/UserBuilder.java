package com.qualrole.backend.user.builder;

import com.qualrole.backend.user.dto.UserDTO;
import com.qualrole.backend.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserBuilder {

    public User buildNewUser(UserDTO userDTO) {
        return new User(
                null,
                userDTO.nome(),
                userDTO.cpfCnpj(),
                userDTO.email(),
                userDTO.telefone(),
                userDTO.endereco(),
                userDTO.dataNascimento(),
                userDTO.role()
        );
    }

    public User updateUser(User existingUser, UserDTO userDTO) {
        existingUser.setEmail(userDTO.email());
        existingUser.setTelefone(userDTO.telefone());
        existingUser.setEndereco(userDTO.endereco());
        existingUser.setDataNascimento(userDTO.dataNascimento());
        return existingUser;
    }
}