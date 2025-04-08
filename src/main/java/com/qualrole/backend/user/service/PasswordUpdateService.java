package com.qualrole.backend.user.service;

import com.qualrole.backend.user.dto.UpdatePasswordDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.InvalidPasswordException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por atualizar a senha do usuario.
 */
@Service
public class PasswordUpdateService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public PasswordUpdateService(SystemUserRepository systemUserRepository,
                                 PasswordEncoder passwordEncoder,
                                 PasswordValidator passwordValidator) {
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Atualiza a senha do usuario autenticado.
     *
     * @param authenticatedUserId ID do usuario autenticado.
     * @param updatePasswordDTO   Objeto com a senha atual e a nova senha.
     * @throws InvalidPasswordException Caso a senha atual não corresponda ou a nova senha seja inválida.
     */
    public void updatePassword(String authenticatedUserId, UpdatePasswordDTO updatePasswordDTO) {
        SystemUser user = systemUserRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(updatePasswordDTO.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("A senha atual está incorreta.");
        }

        passwordValidator.validatePassword(updatePasswordDTO.newPassword());

        user.setPassword(passwordEncoder.encode(updatePasswordDTO.newPassword()));
        systemUserRepository.save(user);
    }
}