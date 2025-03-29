package es.miw.tfm.invierte.user.data.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class SystemRoleTest {

  @Test
  void testSystemRoleValues() {
    SystemRole[] systemRoles = SystemRole.values();
    assertNotNull(systemRoles);
    assertEquals(2, systemRoles.length);
    assertEquals(SystemRole.ADMIN, systemRoles[0]);
    assertEquals(SystemRole.SUPPORT, systemRoles[1]);
  }

  @Test
  void testSystemRoleValueOf() {
    assertEquals(SystemRole.ADMIN, SystemRole.valueOf("ADMIN"));
    assertEquals(SystemRole.SUPPORT, SystemRole.valueOf("SUPPORT"));
  }

}
