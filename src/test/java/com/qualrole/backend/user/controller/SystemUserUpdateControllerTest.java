package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SystemUserUpdateDTO;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.service.SystemUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SystemUserUpdateControllerTest {

    @Mock
    private SystemUserService systemUserService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private SystemUserUpdateController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new SystemUserUpdateController(systemUserService);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        String authenticatedUserId = "12345";
        SystemUserUpdateDTO updateDTO = new SystemUserUpdateDTO(
            "Updated Name",
            "1234567890",
            LocalDate.of(1990, 1, 1),
            new SystemUserUpdateDTO.AddressDTO(
                "Test Street",
                "123",
                "Apt 1",
                "Test Neighborhood",
                "Test City",
                "Test State",
                "12345678"
            )
        );

        SystemUser updatedUser = new SystemUser();
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUserId);
        SecurityContextHolder.setContext(securityContext);

        when(systemUserService.updateSystemUser(eq(authenticatedUserId), any(SystemUserUpdateDTO.class)))
                .thenReturn(updatedUser);

        ResponseEntity<SystemUser> response = controller.updateUser(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(systemUserService, times(1)).updateSystemUser(authenticatedUserId, updateDTO);
    }
}