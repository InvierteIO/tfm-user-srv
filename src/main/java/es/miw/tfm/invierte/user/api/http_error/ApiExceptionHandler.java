package es.miw.tfm.invierte.user.api.http_error;

import es.miw.tfm.invierte.user.service.exception.BadRequestException;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.FatalBeanException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.resource.NoResourceFoundException;

/**
 * Global exception handler for the API.
 * This class handles various exceptions thrown by the application
 * and maps them to appropriate HTTP responses.
 *
 * <p>It provides specific handlers for common exceptions such as
 * `NotFoundException`, `BadRequestException`, and others.
 * Logs errors for internal server exceptions.
 * @see ErrorMessage
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Log4j2
@ControllerAdvice
public class ApiExceptionHandler {

  /**
   * Handles exceptions when a resource is not found.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NoResourceFoundException.class})
  @ResponseBody
  public ErrorMessage noResourceFoundRequest(Exception exception) {
    return new ErrorMessage(new NotFoundException(
        "Path not found. Try : **/actuator/info o **/swagger-ui.html o **/v3/api-docs"),
        HttpStatus.NOT_FOUND.value());
  }

  /**
   * Handles `NotFoundException` exceptions.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NotFoundException.class})
  @ResponseBody
  public ErrorMessage notFoundRequest(Exception exception) {
    return new ErrorMessage(exception, HttpStatus.NOT_FOUND.value());
  }

  /**
   * Handles bad request exceptions such as validation errors.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      BadRequestException.class,
      DuplicateKeyException.class,
      MethodArgumentNotValidException.class,
      HttpMessageNotReadableException.class,
      FatalBeanException.class
      })
  @ResponseBody
  public ErrorMessage badRequest(Exception exception) {
    return new ErrorMessage(exception, HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles conflict exceptions.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({ConflictException.class})
  @ResponseBody
  public ErrorMessage conflict(Exception exception) {
    return new ErrorMessage(exception, HttpStatus.CONFLICT.value());
  }

  /**
   * Handles forbidden access exceptions.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({ForbiddenException.class})
  @ResponseBody
  public ErrorMessage forbidden(Exception exception) {
    return new ErrorMessage(exception, HttpStatus.FORBIDDEN.value());
  }

  /**
   * Handles all other exceptions and logs the error.
   *
   * @param exception the exception thrown
   * @return an ErrorMessage object with details of the error
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ErrorMessage exception(
      Exception exception) {
    log.error("[MIW]::", exception);
    return new ErrorMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

}
