package es.miw.tfm.invierte.user.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class StaffDtoTest {

  private StaffDto staffDto;

  @BeforeEach
  void setUp() {
    staffDto = StaffDto.builder()
        .firstName("Test")
        .familyName("User")
        .email("test@example.com")
        .password("password")
        .companyRole(CompanyRole.OWNER)
        .birthDate(LocalDate.of(1990, 1, 1))
        .identityDocument("12345678A")
        .jobTitle("Developer")
        .address("123 Main St")
        .phone("123456789")
        .gender(Gender.MALE)
        .taxIdentifierNumber("123456789")
        .build();
  }

  @Test
  void testToStaff() {
    Staff staff = staffDto.toStaff();

    assertEquals("Test", staff.getFirstName());
    assertEquals("User", staff.getFamilyName());
    assertEquals("test@example.com", staff.getEmail());
    assertTrue(new BCryptPasswordEncoder().matches("password", staff.getPassword()));
    assertEquals(CompanyRole.OWNER, staff.getCompanyRole());
    assertEquals(LocalDate.of(1990, 1, 1), staff.getBirthDate());
    assertEquals("12345678A", staff.getIdentityDocument());
    assertEquals("Developer", staff.getJobTitle());
    assertEquals("123 Main St", staff.getAddress());
    assertEquals("123456789", staff.getPhone());
    assertEquals(Gender.MALE, staff.getGender());
    assertEquals("123456789", staff.getTaxIdentifierNumber());
  }
}
