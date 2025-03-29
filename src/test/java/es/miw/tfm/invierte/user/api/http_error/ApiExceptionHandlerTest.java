package es.miw.tfm.invierte.user.api.http_error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.miw.tfm.invierte.user.service.exception.BadRequestException;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.resource.NoResourceFoundException;

public class ApiExceptionHandlerTest {

  private ApiExceptionHandler apiExceptionHandler;

  @BeforeEach
  void setUp() {
    apiExceptionHandler = new ApiExceptionHandler();
  }

  @Test
  void testNoResourceFoundRequest() {
    ErrorMessage errorMessage = apiExceptionHandler.noResourceFoundRequest(new NoResourceFoundException("Resource not found"));
    assertEquals("NotFoundException", errorMessage.getError());
    assertTrue(errorMessage.getMessage().contains("Path not found"));
    assertEquals(HttpStatus.NOT_FOUND.value(), errorMessage.getCode());
  }

  @Test
  void testNotFoundRequest() {
    ErrorMessage errorMessage = apiExceptionHandler.notFoundRequest(new NotFoundException("Not found"));
    assertEquals("NotFoundException", errorMessage.getError());
    assertTrue(errorMessage.getMessage().contains("Not found"));
    assertEquals(HttpStatus.NOT_FOUND.value(), errorMessage.getCode());
  }

  @Test
  void testBadRequest() {
    ErrorMessage errorMessage = apiExceptionHandler.badRequest(new BadRequestException("Bad request"));
    assertEquals("BadRequestException", errorMessage.getError());
    assertTrue(errorMessage.getMessage().contains("Bad request"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), errorMessage.getCode());
  }

  @Test
  void testConflict() {
    ErrorMessage errorMessage = apiExceptionHandler.conflict(new ConflictException("Conflict"));
    assertEquals("ConflictException", errorMessage.getError());
    assertTrue(errorMessage.getMessage().contains("Conflict"));
    assertEquals(HttpStatus.CONFLICT.value(), errorMessage.getCode());
  }

  @Test
  void testForbidden() {
    ErrorMessage errorMessage = apiExceptionHandler.forbidden(new ForbiddenException("Forbidden"));
    assertEquals("ForbiddenException", errorMessage.getError());
    assertTrue(errorMessage.getMessage().contains("Forbidden"));
    assertEquals(HttpStatus.FORBIDDEN.value(), errorMessage.getCode());
  }

  @Test
  void testException() {
    ErrorMessage errorMessage = apiExceptionHandler.exception(new Exception("Internal server error"));
    assertEquals("Exception", errorMessage.getError());
    assertEquals("Internal server error", errorMessage.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage.getCode());
  }
}
