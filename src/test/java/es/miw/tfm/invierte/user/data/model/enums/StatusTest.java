package es.miw.tfm.invierte.user.data.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StatusTest {

  @Test
  void testStatusValues() {
    Status[] statuses = Status.values();
    assertNotNull(statuses);
    assertEquals(3, statuses.length);
    assertEquals(Status.ACTIVE, statuses[0]);
    assertEquals(Status.INACTIVE, statuses[1]);
    assertEquals(Status.DELETED, statuses[2]);
  }

  @Test
  void testStatusValueOf() {
    assertEquals(Status.ACTIVE, Status.valueOf("ACTIVE"));
    assertEquals(Status.INACTIVE, Status.valueOf("INACTIVE"));
    assertEquals(Status.DELETED, Status.valueOf("DELETED"));
  }

}
