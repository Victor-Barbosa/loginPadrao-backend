package com.qualrole.backend.user.service;

import com.qualrole.backend.user.dto.UpdatePasswordDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.InvalidPasswordException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordUpdateServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    private PasswordUpdateService passwordUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordUpdateService = new PasswordUpdateService(systemUserRepository, passwordEncoder, passwordValidator);
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        String userId = "12345";
        SystemUser user = new SystemUser();
        user.setPassword("encodedCurrentPassword");

        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("currentPassword",
                "newPassword");

        when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("currentPassword",
                "encodedCurrentPassword")).thenReturn(true);

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        doNothing().when(passwordValidator).validatePassword("newPassword");

        passwordUpdateService.updatePassword(userId, updatePasswordDTO);

        verify(systemUserRepository).findById(userId);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(passwordValidator).validatePassword("newPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(systemUserRepository).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        String userId = "12345";
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("currentPassword",
                "newPassword");

        when(systemUserRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                passwordUpdateService.updatePassword(userId, updatePasswordDTO)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(systemUserRepository).findById(userId);
        verifyNoInteractions(passwordEncoder, passwordValidator);
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenCurrentPasswordDoesNotMatch() {
        String userId = "12345";
        SystemUser user = new SystemUser();
        user.setPassword("encodedCurrentPassword");

        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("wrongPassword",
                "newPassword");

        when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updatePasswordDTO.currentPassword(), user.getPassword())).thenReturn(false);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                passwordUpdateService.updatePassword(userId, updatePasswordDTO)
        );

        assertEquals("A senha atual está incorreta.", exception.getMessage());
        verify(systemUserRepository).findById(userId);
        verify(passwordEncoder).matches(updatePasswordDTO.currentPassword(), user.getPassword());
        verifyNoMoreInteractions(passwordEncoder, passwordValidator, systemUserRepository);
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenNewPasswordIsInvalid() {
        String userId = "12345";
        SystemUser user = new SystemUser();
        user.setPassword("encodedCurrentPassword");

        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO("currentPassword",
                "invalidPassword");

        when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updatePasswordDTO.currentPassword(), user.getPassword())).thenReturn(true);

        doThrow(new InvalidPasswordException("Nova senha inválida"))
                .when(passwordValidator)
                .validatePassword(updatePasswordDTO.newPassword());

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                passwordUpdateService.updatePassword(userId, updatePasswordDTO)
        );

        assertEquals("Nova senha inválida", exception.getMessage());
        verify(systemUserRepository).findById(userId);
        verify(passwordEncoder).matches(updatePasswordDTO.currentPassword(), user.getPassword());
        verify(passwordValidator).validatePassword(updatePasswordDTO.newPassword());
        verifyNoMoreInteractions(passwordEncoder, systemUserRepository);
    }
}