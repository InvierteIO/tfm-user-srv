package es.miw.tfm.invierte.user.service.exception;

/**
 * Exception class for handling bad requests.
 * This exception is thrown when a request cannot be processed due to client-side errors.
 * It extends the `RuntimeException` class.
 *
 * @see java.lang.RuntimeException
 * @see es.miw.tfm.invierte.user.service.exception
 *
 * @author denilssonmn
 */
public class BadRequestException extends RuntimeException {

  private static final String DESCRIPTION = "Bad Request Exception";

  public BadRequestException(String detail) {
    super(DESCRIPTION + ". " + detail);
  }

}
