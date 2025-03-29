package es.miw.tfm.invierte.user.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  private BadRequestException badRequestException;

  @BeforeEach
  void setUp() {
    this.badRequestException = new BadRequestException("detail");
  }

  @Test
  void testGetMessage() {
    assertEquals("Bad Request Exception. detail", this.badRequestException.getMessage());
  }

}
