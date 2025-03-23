package com.qualrole.backend.user.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entidade que representa um usuário do sistema.
 */
@Entity
@Table(name = "system_users")
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "system_user_id")
    private String systemUserId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 15, name = "phone_number")
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    private AddressUser address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column()
    private String password;

    @Enumerated(EnumType.STRING)
    @Column()
    private Role role;

    /**
     * Construtor padrão necessário para o JPA.
     */
    public SystemUser() {
    }

    public SystemUser(String systemUserId, String name, String cpfCnpj, String email, String phoneNumber,
                      AddressUser address, LocalDate birthDate, String password, Role role) {
        this.systemUserId = systemUserId;
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthDate = birthDate;
        this.password = password;
        this.role = role;
    }

    public String getSystemUserId() {
        return systemUserId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}