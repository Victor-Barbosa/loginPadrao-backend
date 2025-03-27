package com.qualrole.backend.user.entity;

import jakarta.persistence.*;

/**
 * Entidade que representa o endereço de um usuario no sistema.
 */
@Entity
@Table(name = "address_users")
public class AddressUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column()
    private String street;

    @Column()
    private String number;

    @Column()
    private String complement;

    @Column()
    private String neighborhood;

    @Column()
    private String city;

    @Column()
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @OneToOne(mappedBy = "address", fetch = FetchType.EAGER)
    private SystemUser systemUser;

    /**
     * Construtor padrão necessário para o JPA.
     */

    public AddressUser() {
    }

    public AddressUser(Long addressId, String street, String number, String complement,
                       String neighborhood, String city, String state, String zipCode, SystemUser systemUser) {
        this.addressId = addressId;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.systemUser = systemUser;
    }
}