package com.qualrole.backend.user.repository;

import com.qualrole.backend.user.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório responsável por operar a persistência de dados da entidade {@link SystemUser}.
 */
public interface SystemUserRepository extends JpaRepository<SystemUser, String> {

    /**
     * Busca um usuario pelo e-mail.
     *
     * @param email e-mail do usuario.
     * @return {@link Optional} contendo o usuário, caso encontre.
     */
    Optional<SystemUser> findByEmail(String email);

    /**
     * Busca um usuario pelo CPF ou CNPJ.
     *
     * @param cpfCnpj CPF ou CNPJ do usuario.
     * @return {@link Optional} contendo o usuário, caso encontre.
     */
    Optional<SystemUser> findByCpfCnpj(String cpfCnpj);
}