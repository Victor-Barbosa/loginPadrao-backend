package com.qualrole.backend.user.builder;

import com.qualrole.backend.user.dto.CompleteSystemUserDTO;
import com.qualrole.backend.user.dto.CompleteSystemUserDTO.AddressDTO;
import com.qualrole.backend.user.entity.AddressUser;
import com.qualrole.backend.user.entity.Role;
import com.qualrole.backend.user.entity.SystemUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserBuilderTest {

    private final UserBuilder userBuilder = new UserBuilder();

    @Test
    void shouldBuildNewCompleteSystemUserWithValidDto() {
        CompleteSystemUserDTO dto = new CompleteSystemUserDTO(
                "John Doe",
                "12345678901",
                "johndoe@example.com",
                "1234567890",
                new AddressDTO(
                        "1234 Elm Street",
                        "56A",
                        "Apt 3",
                        "Downtown",
                        "Metropolis",
                        "NY",
                        "12345"
                ),
                LocalDate.of(1990, 1, 1),
                "SecurePassword123",
                Role.EVENT_CREATOR
        );

        SystemUser systemUser = userBuilder.buildNewCompleteSystemUser(dto);

        assertNotNull(systemUser);
        assertEquals(dto.name(), systemUser.getName());
        assertEquals(dto.cpfCnpj(), systemUser.getCpfCnpj());
        assertEquals(dto.email(), systemUser.getEmail());
        assertEquals(dto.phoneNumber(), systemUser.getPhoneNumber());
        assertEquals(dto.birthDate(), systemUser.getBirthDate());
        assertEquals(dto.password(), systemUser.getPassword());
        assertEquals(Role.EVENT_CREATOR, systemUser.getRole());
        AddressUser address = systemUser.getAddress();
        assertNotNull(address);
        assertEquals(dto.addresses().street(), address.getStreet());
        assertEquals(dto.addresses().number(), address.getNumber());
        assertEquals(dto.addresses().complement(), address.getComplement());
        assertEquals(dto.addresses().neighborhood(), address.getNeighborhood());
        assertEquals(dto.addresses().city(), address.getCity());
        assertEquals(dto.addresses().state(), address.getState());
        assertEquals(dto.addresses().zipCode(), address.getZipCode());
    }

    @Test
    void shouldBuildNewCompleteSystemUserWithEmptyOptionalFieldsInAddress() {
        CompleteSystemUserDTO dto = new CompleteSystemUserDTO(
                "Jane Doe",
                "98765432109",
                "janedoe@example.com",
                "9876543210",
                new AddressDTO(
                        "5678 Oak Street",
                        "78B",
                        null,
                        "Midtown",
                        "Gotham",
                        "CA",
                        "54321"
                ),
                LocalDate.of(1985, 5, 15),
                "AnotherSecurePassword456",
                Role.EVENT_CREATOR
        );

        SystemUser systemUser = userBuilder.buildNewCompleteSystemUser(dto);

        assertNotNull(systemUser);
        assertEquals(dto.name(), systemUser.getName());
        assertEquals(dto.cpfCnpj(), systemUser.getCpfCnpj());
        assertEquals(dto.email(), systemUser.getEmail());
        assertEquals(dto.phoneNumber(), systemUser.getPhoneNumber());
        assertEquals(dto.birthDate(), systemUser.getBirthDate());
        assertEquals(dto.password(), systemUser.getPassword());
        assertEquals(Role.EVENT_CREATOR, systemUser.getRole());
        AddressUser address = systemUser.getAddress();
        assertNotNull(address);
        assertEquals(dto.addresses().street(), address.getStreet());
        assertEquals(dto.addresses().number(), address.getNumber());
        assertNull(address.getComplement());
        assertEquals(dto.addresses().neighborhood(), address.getNeighborhood());
        assertEquals(dto.addresses().city(), address.getCity());
        assertEquals(dto.addresses().state(), address.getState());
        assertEquals(dto.addresses().zipCode(), address.getZipCode());
    }

    @Test
    void shouldSetAddressToNullWhenAddressIsEmpty() {
        CompleteSystemUserDTO dto = new CompleteSystemUserDTO(
                "John Smith",
                "32165498709",
                "johnsmith@example.com",
                "3216549870",
                new AddressDTO(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                ),
                LocalDate.of(2000, 12, 25),
                "Password789",
                Role.EVENT_CREATOR
        );

        SystemUser systemUser = userBuilder.buildNewCompleteSystemUser(dto);

        assertNotNull(systemUser);
        assertEquals(dto.name(), systemUser.getName());
        assertEquals(dto.cpfCnpj(), systemUser.getCpfCnpj());
        assertEquals(dto.email(), systemUser.getEmail());
        assertEquals(dto.phoneNumber(), systemUser.getPhoneNumber());
        assertEquals(dto.birthDate(), systemUser.getBirthDate());
        assertEquals(dto.password(), systemUser.getPassword());
        assertEquals(Role.EVENT_CREATOR, systemUser.getRole());
        AddressUser address = systemUser.getAddress();
        assertNull(address);
    }
}