package es.miw.tfm.invierte.user.service.exception;

/**
 * Exception class for handling conflict errors.
 * This exception is thrown when a request cannot be processed due to a conflict
 * with the current state of the resource.
 * It extends the `RuntimeException` class.
 *
 * @see java.lang.RuntimeException
 * @see es.miw.tfm.invierte.user.service.exception
 *
 * @author denilssonmn
 */
public class ConflictException extends RuntimeException {

  private static final String DESCRIPTION = "Conflict Exception";

  public ConflictException(String detail) {
    super(DESCRIPTION + ". " + detail);
  }

}
