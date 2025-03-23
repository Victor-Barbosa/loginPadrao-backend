package com.qualrole.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe de configuração de segurança da aplicação.
 * Configura o filtro de segurança e o codificador de senha.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configura o filtro de segurança para restringir ou permitir o acesso a endpoints.
     *
     * @param http Instância do {@link HttpSecurity} para configurar os endpoints.
     * @return Objeto {@link SecurityFilterChain} configurado.
     * @throws Exception Caso ocorra algum erro na configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/complete").permitAll()
                        .requestMatchers("/api/users/simple").permitAll()
                        .requestMatchers("/oauth2/**", "/api/users/oauth2/**").permitAll()
                        .requestMatchers("/api/users/oauth2/set-password").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/users/oauth2/success", true)
                        .failureUrl("/api/users/oauth2/failure")
                );

        return http.build();
    }

    /**
     * Define um codificador de senha usando o algoritmo Argon2.
     *
     * @return Instância do {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16,
                32,
                1,
                65536,
                3
        );
    }
}