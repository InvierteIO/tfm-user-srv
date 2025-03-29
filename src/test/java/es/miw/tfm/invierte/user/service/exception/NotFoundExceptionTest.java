package es.miw.tfm.invierte.user.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

  private static final String DESCRIPTION = "Not Found Exception";

  private static final String DETAIL = "detail";

  private NotFoundException notFoundException;

  @BeforeEach
  void setUp() {
    notFoundException = new NotFoundException(DETAIL);
  }

  @Test
  void testNotFoundException() {
    assertEquals(DESCRIPTION + ". " + DETAIL, notFoundException.getMessage());
  }

}
