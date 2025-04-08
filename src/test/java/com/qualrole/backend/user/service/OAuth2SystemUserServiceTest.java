package com.qualrole.backend.user.service;

import com.qualrole.backend.user.builder.UserBuilder;
import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.exception.InvalidUserRoleException;
import com.qualrole.backend.user.exception.MissingRequiredFieldsException;
import com.qualrole.backend.exception.UserNotFoundException;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        SecurityContextHolder.clearContext();
    }

    @Test
    void testPromoteGuestToStandardUser_Success() {
        String authenticatedUserId = "authenticated-user-id";

        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO(
                "User Name", "test@example.com", "123456789", "securePassword123",
                null, null, null, null
        );

        SystemUser guestUser = new SystemUser();
        ReflectionTestUtils.setField(guestUser, "systemUserId", authenticatedUserId);
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.of(guestUser));
        when(passwordEncoder.encode(simpleDto.password())).thenReturn("encodedPassword");

        oAuth2SystemUserService.promoteGuestToStandardUser(authenticatedUserId, simpleDto);

        verify(systemUserRepository).findById(authenticatedUserId);
        verify(userBuilder).updateOAuthSystemUser(guestUser, simpleDto);
        verify(passwordValidator).validatePassword(simpleDto.password());
        verify(passwordEncoder).encode(simpleDto.password());
        verify(systemUserRepository).save(guestUser);

        assertEquals(Role.STANDARD_USER, guestUser.getRole());
        assertEquals("encodedPassword", guestUser.getPassword());
    }

    @Test
    void testPromoteGuestToStandardUser_GuestNotFound() {
        String authenticatedUserId = "authenticated-user-id";

        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO(
                "User Name", "test@example.com", "123456789", "securePassword123",
                null, null, null, null
        );

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(authenticatedUserId, simpleDto)
        );

        assertEquals("Usuário não encontrado com o ID autenticado.", exception.getMessage());
        verify(systemUserRepository).findById(authenticatedUserId);
        verifyNoInteractions(passwordValidator, passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_InvalidRole() {
        String authenticatedUserId = "authenticated-user-id";

        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO(
                "User Name", "test@example.com", "123456789", "securePassword123",
                null, null, null, null
        );

        SystemUser standardUser = new SystemUser();
        ReflectionTestUtils.setField(standardUser, "systemUserId", authenticatedUserId);
        standardUser.setRole(Role.STANDARD_USER);

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.of(standardUser));

        InvalidUserRoleException exception = assertThrows(InvalidUserRoleException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(authenticatedUserId, simpleDto)
        );

        assertEquals("Somente Usuários com a role GUEST podem ser promovidos.", exception.getMessage());
        verify(systemUserRepository).findById(authenticatedUserId);
        verifyNoInteractions(passwordValidator, passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_MissingFields() {
        String authenticatedUserId = "authenticated-user-id";

        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO(
                null, "test@example.com", "123456789", "securePassword123",
                null, null, null, null
        );

        SystemUser guestUser = new SystemUser();
        ReflectionTestUtils.setField(guestUser, "systemUserId", authenticatedUserId);
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.of(guestUser));

        MissingRequiredFieldsException exception = assertThrows(MissingRequiredFieldsException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(authenticatedUserId, simpleDto)
        );

        assertEquals("O nome é obrigatório para completar o cadastro.", exception.getMessage());
        verify(systemUserRepository).findById(authenticatedUserId);
        verifyNoInteractions(passwordEncoder, userBuilder);
    }

    @Test
    void testPromoteGuestToStandardUser_InvalidPassword() {
        String authenticatedUserId = "authenticated-user-id";

        SimpleSystemUserDTO simpleDto = new SimpleSystemUserDTO(
                "User Name", "test@example.com", "123456789", "weakPassword",
                null, null, null, null
        );

        SystemUser guestUser = new SystemUser();
        ReflectionTestUtils.setField(guestUser, "systemUserId", authenticatedUserId);
        guestUser.setRole(Role.GUEST);

        when(systemUserRepository.findById(authenticatedUserId)).thenReturn(Optional.of(guestUser));
        doThrow(new IllegalArgumentException("Senha não é forte o suficiente."))
                .when(passwordValidator).validatePassword(simpleDto.password());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                oAuth2SystemUserService.promoteGuestToStandardUser(authenticatedUserId, simpleDto)
        );

        assertEquals("Senha não é forte o suficiente.", exception.getMessage());
        verify(systemUserRepository).findById(authenticatedUserId);
        verify(passwordValidator).validatePassword(simpleDto.password());
        verifyNoInteractions(passwordEncoder, userBuilder);
    }
}