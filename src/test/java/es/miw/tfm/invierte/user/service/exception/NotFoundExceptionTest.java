package es.miw.tfm.invierte.user.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

  private static final String DESCRIPTION = "Not Found Exception";

  private static final String DETAIL = "detail";

  private NotFoundException notFoundException;

  @BeforeEach
  public void setUp() {
    notFoundException = new NotFoundException(DETAIL);
  }

  @Test
  public void testNotFoundException() {
    assertEquals(DESCRIPTION + ". " + DETAIL, notFoundException.getMessage());
  }

}
