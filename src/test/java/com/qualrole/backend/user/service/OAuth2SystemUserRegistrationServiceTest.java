package com.qualrole.backend.user.service;

import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import com.qualrole.backend.user.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class OAuth2SystemUserRegistrationServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private OAuth2SystemUserRegistrationService registrationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterOAuth2UserSuccessfully() {
        // Arrange
        String email = "user@example.com";
        String name = "Test User";

        SystemUser savedUser = new SystemUser();
        savedUser.setEmail(email);
        savedUser.setName(name);
        savedUser.setPassword(null);
        savedUser.setRole(Role.GUEST);

        when(systemUserRepository.save(any(SystemUser.class))).thenReturn(savedUser);

        SystemUser result = registrationService.registerOAuth2User(email, name);

        verify(userValidator, times(1)).validateEmailUniqueness(email, null);

        ArgumentCaptor<SystemUser> userCaptor = ArgumentCaptor.forClass(SystemUser.class);
        verify(systemUserRepository, times(1)).save(userCaptor.capture());

        SystemUser capturedUser = userCaptor.getValue();
        assertEquals(email, capturedUser.getEmail());
        assertEquals(name, capturedUser.getName());
        assertNull(capturedUser.getPassword());
        assertEquals(Role.GUEST, capturedUser.getRole());

        assertEquals(savedUser, result);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotUnique() {
        String email = "duplicate@example.com";
        String name = "Test User";

        doThrow(new IllegalArgumentException("Email already in use"))
                .when(userValidator).validateEmailUniqueness(email, null);

        try {
            registrationService.registerOAuth2User(email, name);
        } catch (IllegalArgumentException e) {
            assertEquals("Email already in use", e.getMessage());
        }

        verify(userValidator, times(1)).validateEmailUniqueness(email, null);
        verify(systemUserRepository, never()).save(any(SystemUser.class));
    }
}