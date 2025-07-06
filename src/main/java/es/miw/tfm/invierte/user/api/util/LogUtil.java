package es.miw.tfm.invierte.user.api.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for logging-related helper methods.
 * Provides methods to sanitize log messages by removing
 * line breaks and tabs.
 *
 * @author denilssonmn
 */
@UtilityClass
@Slf4j
public class LogUtil {

  /**
   * Sanitizes the input string by replacing line breaks and tabs
   * with underscores. Returns null if input is null.
   *
   * @param input the string to sanitize
   * @return the sanitized string or null if input is null
   */
  public static String sanitize(String input) {
    return input == null ? null : input.replaceAll("[\\n\\r\\t]", "_");
  }

}
