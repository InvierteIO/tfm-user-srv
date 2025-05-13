package es.miw.tfm.invierte.user.service.exception;

/**
 * Exception class for handling forbidden access errors.
 * This exception is thrown when a request is denied due to insufficient permissions
 * or access rights.
 * It extends the `RuntimeException` class.
 *
 * @see java.lang.RuntimeException
 * @see es.miw.tfm.invierte.user.service.exception
 *
 * @author denilssonmn
 */
public class ForbiddenException extends RuntimeException {

  private static final String DESCRIPTION = "Forbidden Exception";

  public ForbiddenException(String detail) {
    super(DESCRIPTION + ". " + detail);
  }

}
