package es.miw.tfm.invierte.user.api.http_error;

import lombok.Getter;
import lombok.ToString;

/**
 * Represents an error message for API responses.
 * This class encapsulates details about an error, including
 * the error type, message, and HTTP status code.
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Getter
@ToString
public class ErrorMessage {

  private final String error;

  private final String message;

  private final Integer code;

  /**
   * Constructs an `ErrorMessage` object.
   *
   * @param exception the exception that caused the error
   * @param code the HTTP status code associated with the error
   */
  public ErrorMessage(Exception exception, Integer code) {
    this.error = exception.getClass().getSimpleName();
    this.message = exception.getMessage();
    this.code = code;
  }

}
