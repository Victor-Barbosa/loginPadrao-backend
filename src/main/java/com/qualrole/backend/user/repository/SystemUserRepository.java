package com.qualrole.backend.user.repository;

import com.qualrole.backend.user.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório responsável por operar a persistência de dados da entidade {@link SystemUser}.
 */
public interface SystemUserRepository extends JpaRepository<SystemUser, String> {

    /**
     * Busca um usuário pelo e-mail.
     *
     * @param email Email do usuário.
     * @return {@link Optional} contendo o usuário, caso encontre.
     */
    Optional<SystemUser> findByEmail(String email);

    /**
     * Busca um usuário pelo CPF ou CNPJ.
     *
     * @param cpfCnpj CPF ou CNPJ do usuário.
     * @return {@link Optional} contendo o usuário, caso encontre.
     */
    Optional<SystemUser> findByCpfCnpj(String cpfCnpj);
}