package es.miw.tfm.invierte.user.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConflictExceptionTest {

  private static final String DESCRIPTION = "Conflict Exception";

  private static final String DETAIL = "detail";

  private static final String EXPECTED_MESSAGE = DESCRIPTION + ". " + DETAIL;

  private ConflictException conflictException;

  @BeforeEach
  public void setUp() {
    this.conflictException = new ConflictException(DETAIL);
  }

  @Test
  public void testConflictException() {
    assertEquals(EXPECTED_MESSAGE, this.conflictException.getMessage());
  }

}
