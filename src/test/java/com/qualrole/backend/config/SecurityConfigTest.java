package com.qualrole.backend.config;

import com.qualrole.backend.auth.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldLoadSecurityFilterChain() {
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void shouldUseArgon2PasswordEncoder() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(PasswordEncoder.class);
    }

    @Test
    void shouldLoadJwtAuthenticationFilter() {
        assertThat(jwtAuthenticationFilter).isNotNull();
    }

    @Test
    void shouldEncodeAndVerifyPassword() {
        String rawPassword = "senha123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}