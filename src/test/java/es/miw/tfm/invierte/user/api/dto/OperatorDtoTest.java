package es.miw.tfm.invierte.user.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class OperatorDtoTest {

  private OperatorDto operatorDto;

  @BeforeEach
  void setUp() {
    operatorDto = OperatorDto.builder()
        .firstName("Test")
        .familyName("User")
        .email("test@example.com")
        .build();
  }

  @Test
  void testDoDefault() {
    operatorDto.doDefault();
    assertNotNull(operatorDto.getPassword());
    assertEquals(SystemRole.SUPPORT, operatorDto.getSystemRole());
  }

  @Test
  void testToOperator() {
    operatorDto.setPassword("password");
    operatorDto.setSystemRole(SystemRole.ADMIN);

    Operator operator = operatorDto.toOperator();

    assertEquals("Test", operator.getFirstName());
    assertEquals("User", operator.getFamilyName());
    assertEquals("test@example.com", operator.getEmail());
    assertTrue(new BCryptPasswordEncoder().matches("password", operator.getPassword()));
    assertEquals(SystemRole.ADMIN, operator.getSystemRole());
  }

}
