package com.qualrole.backend.user.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;

        @Column(nullable = false)
        private String nome;

        @Column(unique = true, nullable = false)
        private String cpfCnpj;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(length = 11, nullable = false)
        private String telefone;

        @Column(length = 500, nullable = false)
        private String endereco;

        @Column(nullable = false)
        private LocalDate dataNascimento;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        public User() {
        }

        public User(String id, String nome, String cpfCnpj, String email, String telefone, String endereco, LocalDate dataNascimento, Role role) {
                this.id = id;
                this.nome = nome;
                this.cpfCnpj = cpfCnpj;
                this.email = email;
                this.telefone = telefone;
                this.endereco = endereco;
                this.dataNascimento = dataNascimento;
                this.role = role;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getNome() {
                return nome;
        }

        public void setNome(String nome) {
                this.nome = nome;
        }

        public String getCpfCnpj() {
                return cpfCnpj;
        }

        public void setCpfCnpj(String cpfCnpj) {
                this.cpfCnpj = cpfCnpj;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getTelefone() {
                return telefone;
        }

        public void setTelefone(String telefone) {
                this.telefone = telefone;
        }

        public String getEndereco() {
                return endereco;
        }

        public void setEndereco(String endereco) {
                this.endereco = endereco;
        }

        public LocalDate getDataNascimento() {
                return dataNascimento;
        }

        public void setDataNascimento(LocalDate dataNascimento) {
                this.dataNascimento = dataNascimento;
        }

        public Role getRole() {
                return role;
        }

        public void setRole(Role role) {
                this.role = role;
        }
}