package es.miw.tfm.invierte.user.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForbiddenExceptionTest {

  private static final String DESCRIPTION = "Forbidden Exception";

  private static final String DETAIL = "detail";

  private ForbiddenException forbiddenException;

  @BeforeEach
  public void setUp() {
    forbiddenException = new ForbiddenException(DETAIL);
  }

  @Test
  public void testForbiddenException() {
    assertEquals(DESCRIPTION + ". " + DETAIL, forbiddenException.getMessage());
  }

}
