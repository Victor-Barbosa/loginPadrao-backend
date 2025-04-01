package com.qualrole.backend.auth.security;

import com.qualrole.backend.auth.exception.EmailNotFoundException;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String email = "test@example.com";
        String password = "password";
        Role role = Role.GUEST;

        SystemUser mockUser = new SystemUser();
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRole(role);

        Mockito.when(systemUserRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo(role.name());
    }

    @Test
    void loadUserByUsername_ShouldThrowEmailNotFoundException_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";

        Mockito.when(systemUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(EmailNotFoundException.class)
                .hasMessage("Usuário não encontrado com o e-mail: " + email);
    }
}