package es.miw.tfm.invierte.user.service.exception;

/**
 * Exception class for handling not found errors.
 * This exception is thrown when a requested resource cannot be found.
 * It extends the `RuntimeException` class.
 *
 * @see java.lang.RuntimeException
 * @see es.miw.tfm.invierte.user.service.exception
 *
 * @author denilssonmn
 */
public class NotFoundException extends RuntimeException {

  private static final String DESCRIPTION = "Not Found Exception";

  public NotFoundException(String detail) {
    super(DESCRIPTION + ". " + detail);
  }

}
