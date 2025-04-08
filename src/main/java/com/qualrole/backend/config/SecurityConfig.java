package com.qualrole.backend.config;

import com.qualrole.backend.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração de segurança da aplicação.
 * Configura o filtro de segurança e o codificador de senha.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

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
                .csrf(AbstractHttpConfigurer::disable) // Desativa CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/complete",
                                "/api/users/simple",
                                "/oauth2/**",
                                "/api/users/oauth2/success",
                                "/api/users/oauth2/failure"
                        ).permitAll()
                        .requestMatchers("/api/complete/registration").hasRole("GUEST")
                        .requestMatchers("/api/users/update").hasAnyRole(
                                "ADMIN", "EVENT_CREATOR", "STANDARD_USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/users/oauth2/success", true)
                        .failureUrl("/api/users/oauth2/failure")
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response,
                                                   authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Acesso nao autorizado\"}");
                        })
                        .accessDeniedHandler((request, response,
                                              accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Acesso negado\"}");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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