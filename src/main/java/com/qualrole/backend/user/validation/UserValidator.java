package com.qualrole.backend.user.validation;

import com.qualrole.backend.exception.CpfOrCnpjAlreadyInUseException;
import com.qualrole.backend.exception.EmailAlreadyInUseException;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Componente responsável por validar informações de usuarios.
 */
@Component
public class UserValidator {

    private final SystemUserRepository systemUserRepository;

    public UserValidator(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    /**
     * Valida a unicidade de CPF ou CNPJ.
     *
     * @param cpfCnpj        CPF ou CNPJ do usuario.
     * @param userIdToIgnore “ID” do usuario a ser ignorado na validação (para evitar conflitos em atualizações).
     * @throws CpfOrCnpjAlreadyInUseException Se o CPF ou CNPJ já estiver em uso.
     */
    public void validateCpfCnpjUniqueness(String cpfCnpj, String userIdToIgnore) {
        Optional<SystemUser> existingUser = systemUserRepository.findByCpfCnpj(cpfCnpj);
        if (existingUser.isPresent() && !existingUser.get().getSystemUserId().equals(userIdToIgnore)) {
            throw new CpfOrCnpjAlreadyInUseException("CPF/CNPJ já está em uso.");
        }
    }

    /**
     * Valida a unicidade do e-mail.
     *
     * @param email          E-mail do usuario.
     * @param userIdToIgnore “ID” do usuario a ser ignorado na validação (para evitar conflitos em atualizações).
     * @throws EmailAlreadyInUseException Se o e-mail já estiver em uso.
     */
    public void validateEmailUniqueness(String email, String userIdToIgnore) {
        Optional<SystemUser> existingUser = systemUserRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getSystemUserId().equals(userIdToIgnore)) {
            throw new EmailAlreadyInUseException("Email já está em uso.");
        }
    }
}