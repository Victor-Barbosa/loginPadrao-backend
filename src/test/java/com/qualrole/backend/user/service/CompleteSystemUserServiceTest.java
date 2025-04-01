package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import com.qualrole.backend.user.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CompleteSystemUserServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private UserBuilder userBuilder;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CompleteSystemUserService completeSystemUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        completeSystemUserService = new CompleteSystemUserService(
                systemUserRepository,
                userValidator,
                passwordValidator,
                userBuilder,
                passwordEncoder
        );
    }

    @Test
    void testRegisterCompleteSystemUser_Success() {
        CompleteSystemUserDTO userDTO = new CompleteSystemUserDTO(
                "John Doe",
                "123456789",
                "test@example.com",
                "1234567890",
                new CompleteSystemUserDTO.AddressDTO(
                        "Street Name",
                        "123",
                        null,
                        "Neighborhood",
                        "City",
                        "State",
                        "12345678"
                ),
                LocalDate.of(1990, 1, 1),
                "password123",
                Role.EVENT_CREATOR
        );
        SystemUser user = new SystemUser();

        doNothing().when(userValidator).validateCpfCnpjUniqueness(userDTO.cpfCnpj(), null);
        doNothing().when(userValidator).validateEmailUniqueness(userDTO.email(), null);
        doNothing().when(passwordValidator).validatePassword(userDTO.password());
        when(userBuilder.buildNewCompleteSystemUser(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(systemUserRepository.save(user)).thenReturn(user);

        completeSystemUserService.registerCompleteSystemUser(userDTO);

        verify(userValidator).validateCpfCnpjUniqueness(userDTO.cpfCnpj(), null);
        verify(userValidator).validateEmailUniqueness(userDTO.email(), null);
        verify(passwordValidator).validatePassword(userDTO.password());
        verify(userBuilder).buildNewCompleteSystemUser(userDTO);
        verify(passwordEncoder).encode(userDTO.password());
        verify(systemUserRepository).save(user);
    }

    @Test
    void testRegisterCompleteSystemUser_EmailUniquenessFails() {
        // Arrange
        CompleteSystemUserDTO userDTO = new CompleteSystemUserDTO(
                "John Doe",
                "123456789",
                "test@example.com",
                "1234567890",
                new CompleteSystemUserDTO.AddressDTO(
                        "Street Name",
                        "123",
                        null,
                        "Neighborhood",
                        "City",
                        "State",
                        "12345678"
                ),
                LocalDate.of(1990, 1, 1),
                "password123",
                Role.EVENT_CREATOR
        );

        doThrow(new RuntimeException("Email already in use"))
                .when(userValidator).validateEmailUniqueness(userDTO.email(), null);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () ->
                completeSystemUserService.registerCompleteSystemUser(userDTO)
        );

        assertEquals("Email already in use", thrownException.getMessage());

        verify(userValidator).validateEmailUniqueness(userDTO.email(), null);
        verify(userValidator).validateCpfCnpjUniqueness(userDTO.cpfCnpj(), null);
        verify(passwordValidator, never()).validatePassword(anyString());
        verify(userBuilder, never()).buildNewCompleteSystemUser(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(systemUserRepository, never()).save(any(SystemUser.class));
    }
}