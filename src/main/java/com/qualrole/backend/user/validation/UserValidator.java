package com.qualrole.backend.user.validation;

import com.qualrole.backend.exception.CpfCnpjAlreadyInUseException;
import com.qualrole.backend.exception.EmailAlreadyInUseException;
import com.qualrole.backend.user.entity.User;
import com.qualrole.backend.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateCpfCnpjUniqueness(String cpfCnpj, String userIdToIgnore) {
        Optional<User> existingUser = userRepository.findByCpfCnpj(cpfCnpj);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userIdToIgnore)) {
            throw new CpfCnpjAlreadyInUseException("CPF/CNPJ já está em uso.");
        }
    }

    public void validateEmailUniqueness(String email, String userIdToIgnore) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userIdToIgnore)) {
            throw new EmailAlreadyInUseException("Email já está em uso.");
        }
    }
}