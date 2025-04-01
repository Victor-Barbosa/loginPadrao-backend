package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import com.qualrole.backend.user.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SimpleSystemUserServiceTest {

    private SystemUserRepository systemUserRepository;
    private UserValidator userValidator;
    private PasswordValidator passwordValidator;
    private UserBuilder userBuilder;
    private PasswordEncoder passwordEncoder;
    private SimpleSystemUserService simpleSystemUserService;

    @BeforeEach
    void setUp() {
        systemUserRepository = mock(SystemUserRepository.class);
        userValidator = mock(UserValidator.class);
        passwordValidator = mock(PasswordValidator.class);
        userBuilder = mock(UserBuilder.class);
        passwordEncoder = mock(PasswordEncoder.class);

        simpleSystemUserService = new SimpleSystemUserService(
                systemUserRepository,
                userValidator,
                passwordValidator,
                userBuilder,
                passwordEncoder
        );
    }

    @Test
    void shouldRegisterSimpleSystemUserSuccessfully() {
        SimpleSystemUserDTO userDTO = new SimpleSystemUserDTO(
                "test@test.com",
                null,
                "123.456.789-10",
                "strongpassword",
                null,
                null,
                null,
                null
        );

        SystemUser expectedUser = new SystemUser();
        expectedUser.setEmail("test@test.com");
        expectedUser.setCpfCnpj("123.456.789-10");

        when(userBuilder.buildNewSimpleSystemUser(userDTO)).thenReturn(expectedUser);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("hashedPassword");

        simpleSystemUserService.registerSimpleSystemUser(userDTO);

        verify(userValidator).validateCpfCnpjUniqueness(userDTO.cpfCnpj(), null);
        verify(userValidator).validateEmailUniqueness(userDTO.email(), null);
        verify(passwordValidator).validatePassword(userDTO.password());

        verify(userBuilder).buildNewSimpleSystemUser(userDTO);
        verify(passwordEncoder).encode(userDTO.password());

        ArgumentCaptor<SystemUser> userCaptor = ArgumentCaptor.forClass(SystemUser.class);
        verify(systemUserRepository).save(userCaptor.capture());

        SystemUser capturedUser = userCaptor.getValue();
        assertEquals("test@test.com", capturedUser.getEmail());
        assertEquals("123.456.789-10", capturedUser.getCpfCnpj());
        assertEquals("hashedPassword", capturedUser.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNotUnique() {
        SimpleSystemUserDTO userDTO = new SimpleSystemUserDTO(
                "duplicate@test.com",
                null,
                "123.456.789-10",
                "weakpassword",
                null,
                null,
                null,
                null
        );

        doThrow(new IllegalArgumentException("Email already exists"))
                .when(userValidator).validateEmailUniqueness(userDTO.email(), null);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> simpleSystemUserService.registerSimpleSystemUser(userDTO)
                );

        assertEquals("Email already exists", exception.getMessage());
        verify(userValidator).validateEmailUniqueness(userDTO.email(), null);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        SimpleSystemUserDTO userDTO = new SimpleSystemUserDTO(
                "test@test.com",
                "123.456.789-10",
                "weakpassword",
                null,
                null,
                null,
                null,
                null
        );

        doThrow(new IllegalArgumentException("Weak password"))
                .when(passwordValidator).validatePassword(userDTO.password());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> simpleSystemUserService.registerSimpleSystemUser(userDTO)
                );

        assertEquals("Weak password", exception.getMessage());
        verify(passwordValidator).validatePassword(userDTO.password());
    }
}