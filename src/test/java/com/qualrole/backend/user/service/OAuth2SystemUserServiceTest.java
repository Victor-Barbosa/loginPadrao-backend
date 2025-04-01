package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.GuestUserNotFoundException;
import com.qualrole.backend.user.exception.InvalidUserRoleException;
import com.qualrole.backend.user.exception.MissingRequiredFieldsException;
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

class OAuth2SystemUserServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserBuilder userBuilder;

    private OAuth2SystemUserService oAuth2SystemUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        oAuth2SystemUserService = new OAuth2SystemUserService(
                systemUserRepository,
                passwordValidator,
                passwordEncoder,
                userBuilder
        );
    }

    @Test
    void testPromoteGuestToStandardUser_Success() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "New Name",
                "123456789", "securePass123", null, null,
                null, null);
        SystemUser guestUser = new SystemUser();
        guestUser.setEmail(simpleDto.email());
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.of(guestUser));
        when(passwordEncoder.encode(simpleDto.password())).thenReturn("encodedPassword");

        oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto);

        verify(systemUserRepository).findByEmail(simpleDto.email());
        verify(userBuilder).updateOAuthSystemUser(guestUser, simpleDto);
        verify(passwordValidator).validatePassword(simpleDto.password());
        verify(passwordEncoder).encode(simpleDto.password());
        verify(systemUserRepository).save(guestUser);

        assertEquals(Role.STANDARD_USER, guestUser.getRole());
        assertEquals("encodedPassword", guestUser.getPassword());
    }

    @Test
    void testPromoteGuestToStandardUser_GuestNotFound() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "New Name",
                "123456789", "securePass123", null,
                null, null, null);
        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.empty());

        GuestUserNotFoundException exception = assertThrows(GuestUserNotFoundException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto)
        );

        assertEquals("Usuário GUEST não encontrado.", exception.getMessage());
        verify(systemUserRepository).findByEmail(simpleDto.email());
        verifyNoInteractions(userBuilder, passwordValidator, passwordEncoder);
    }

    @Test
    void testPromoteGuestToStandardUser_InvalidRole() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "New Name",
                "123456789", "securePass123", null, null,
                null, null);
        SystemUser standardUser = new SystemUser();
        standardUser.setRole(Role.STANDARD_USER);

        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.of(standardUser));

        InvalidUserRoleException exception = assertThrows(InvalidUserRoleException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto)
        );

        assertEquals("Somente usuários com a role GUEST podem ser promovidos.", exception.getMessage());
        verify(systemUserRepository).findByEmail(simpleDto.email());
        verifyNoInteractions(passwordValidator, passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_MissingPhoneNumber() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "User Name",
                null, "securePass123", null, null,
                null, null); // Telefone ausente
        SystemUser guestUser = new SystemUser();
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.of(guestUser));

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto)
        );

        assertTrue(exception.getMessage().contains("O telefone é obrigatório para completar o cadastro."));
        verify(systemUserRepository).findByEmail(simpleDto.email());
        verifyNoInteractions(passwordValidator, passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_MissingPassword() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "User Name",
                "123456789", null, null, null, null, null);
        SystemUser guestUser = new SystemUser();
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.of(guestUser));

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto)
        );

        assertTrue(exception.getMessage().contains("A senha é obrigatória para completar o cadastro."));
        verify(systemUserRepository).findByEmail(simpleDto.email());
        verifyNoInteractions(passwordValidator, passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_InvalidPassword() {
        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO("test@example.com", "Name",
                "123456789", "weakpassword", null, null,
                null, null);
        SystemUser guestUser = new SystemUser();
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findByEmail(simpleDto.email())).thenReturn(Optional.of(guestUser));
        doThrow(new RuntimeException("Senha inválida")).when(passwordValidator).validatePassword(
                simpleDto.password());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(simpleDto)
        );

        assertEquals("Senha inválida", exception.getMessage());
        verify(systemUserRepository).findByEmail(simpleDto.email());
        verify(passwordValidator).validatePassword(simpleDto.password());
        verifyNoInteractions(passwordEncoder, userBuilder);
    }
}