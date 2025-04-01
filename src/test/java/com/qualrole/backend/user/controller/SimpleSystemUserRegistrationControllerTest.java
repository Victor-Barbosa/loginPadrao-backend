package com.qualrole.backend.user.controller;

import com.qualrole.backend.user.dto.SimpleSystemUserDTO;
import com.qualrole.backend.user.service.SimpleSystemUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SimpleSystemUserRegistrationControllerTest {

    @Mock
    private SimpleSystemUserService simpleSystemUserService;

    private SimpleSystemUserRegistrationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new SimpleSystemUserRegistrationController(simpleSystemUserService);
    }

    @Test
    void shouldRegisterSimpleUserSuccessfully() {
        SimpleSystemUserDTO mockDto = new SimpleSystemUserDTO(
                "Test User",
                "test@example.com",
                "1234567890",
                "password",
                null,
                null,
                null,
                null
        );

        doNothing().when(simpleSystemUserService).registerSimpleSystemUser(any(SimpleSystemUserDTO.class));

        ResponseEntity<String> response = controller.registerSimpleUser(mockDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário cadastrado com sucesso!", response.getBody());
        verify(simpleSystemUserService, times(1)).registerSimpleSystemUser(mockDto);
    }
}