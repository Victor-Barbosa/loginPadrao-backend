package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SystemUserUpdateDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SystemUserServiceTest {

    private SystemUserRepository systemUserRepository;
    private UserBuilder userBuilder;
    private SystemUserService systemUserService;

    @BeforeEach
    void setUp() {
        systemUserRepository = mock(SystemUserRepository.class);
        userBuilder = mock(UserBuilder.class);
        systemUserService = new SystemUserService(systemUserRepository, userBuilder);
    }

    @Test
    void shouldUpdateSystemUserSuccessfully() {
        String authenticatedUserId = "user123";
        SystemUserUpdateDTO updateDTO = new SystemUserUpdateDTO("New Name", "newemail@test.com",
                null, null);

        SystemUser existingUser = new SystemUser();
        existingUser.setEmail("oldemail@test.com");
        existingUser.setName("Old Name");

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.of(existingUser));
        when(systemUserRepository.save(existingUser)).thenReturn(existingUser);

        SystemUser updatedUser = systemUserService.updateSystemUser(authenticatedUserId, updateDTO);

        verify(userBuilder).updateSystemUser(existingUser, updateDTO);
        verify(systemUserRepository).save(existingUser);

        assertEquals("Old Name", existingUser.getName());
        assertEquals("oldemail@test.com", existingUser.getEmail());
        assertEquals(existingUser, updatedUser);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        String authenticatedUserId = "nonExistentUser";
        SystemUserUpdateDTO updateDTO = new SystemUserUpdateDTO("Name", "email@test.com",
                null, null);

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> systemUserService.updateSystemUser(authenticatedUserId, updateDTO)
        );

        assertEquals("Usuário não encontrado com o ID autenticado.", exception.getMessage());

        verify(systemUserRepository).findById(authenticatedUserId);

        verifyNoInteractions(userBuilder);

        verify(systemUserRepository, never()).save(any());
    }
}