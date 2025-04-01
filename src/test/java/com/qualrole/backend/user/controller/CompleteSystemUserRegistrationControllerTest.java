package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.service.CompleteSystemUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class CompleteSystemUserRegistrationControllerTest {

    @Mock
    private CompleteSystemUserService completeSystemUserService;

    private CompleteSystemUserRegistrationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new CompleteSystemUserRegistrationController(completeSystemUserService);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CompleteSystemUserDTO mockDto = new CompleteSystemUserDTO(
                "Test User",
                "12345678901",
                "test@example.com",
                "1234567890",
                new CompleteSystemUserDTO.AddressDTO(
                        "Test Street",
                        "123",
                        null,
                        "Test Neighborhood",
                        "Test City",
                        "Test State",
                        "12345678"
                ),
                LocalDate.of(2000, 1, 1),
                "password123",
                Role.EVENT_CREATOR
        );


        doNothing().when(completeSystemUserService).registerCompleteSystemUser(any(CompleteSystemUserDTO.class));

        ResponseEntity<String> response = controller.createUser(mockDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Usuário cadastrado com sucesso!", response.getBody());
        verify(completeSystemUserService).registerCompleteSystemUser(mockDto);
    }
}