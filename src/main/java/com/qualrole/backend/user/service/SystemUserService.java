package com.qualrole.backend.user.service;

import com.qualrole.backend.user.exception.UserNotFoundException;
import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SystemUserUpdateDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemUserService {

    private final SystemUserRepository systemUserRepository;

    private final UserBuilder userBuilder;

    public SystemUserService(SystemUserRepository systemUserRepository, UserBuilder userBuilder) {
        this.systemUserRepository = systemUserRepository;
        this.userBuilder = userBuilder;
    }

    public SystemUser updateSystemUser(String authenticatedUserId, SystemUserUpdateDTO updateDTO) {
        SystemUser existingUser = systemUserRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o ID autenticado."));

        userBuilder.updateSystemUser(existingUser, updateDTO);
        return systemUserRepository.save(existingUser);
    }
}