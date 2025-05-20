package es.miw.tfm.invierte.user.configuration.util;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Utility class for handling security-related operations.
 * This class provides methods to check roles and permissions for a specific company code.
 * It uses Spring Security's reactive context to retrieve authentication details.
 *
 * <p>This class is marked as a service and uses a private constructor to prevent instantiation.
 *
 * @author denilssonmn
 * @author devcastlecix
 */
@Service("securityUtil")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

  /**
   * Checks if the current user has a specific role for a given company code.
   * Retrieves the security context reactively and evaluates the user's authorities.
   *
   * @param role the role to check
   * @param companyCode the company code to check
   * @return a Mono emitting true if the user has the role for the company code, false otherwise
   */
  public static Boolean hasRoleForCompanyCode(String role, String companyCode) {
    final var auth = SecurityContextHolder
        .getContext()
        .getAuthentication();
    return isRoleForCompanyCode(role, companyCode, auth.getAuthorities());
  }

  /**
   * Helper method to check if a role is associated with a specific company code
   * in the user's granted authorities.
   *
   * @param role the role to check
   * @param companyCode the company code to check
   * @param authorities the collection of granted authorities
   * @return true if the role is found for the company code, false otherwise
   */
  private static Boolean isRoleForCompanyCode(String role, String companyCode,
      Collection<? extends GrantedAuthority> authorities) {
    return Stream.ofNullable(authorities)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(GrantedAuthority::getAuthority)
        .anyMatch(companyRole -> companyRole.contains(companyCode + "_" + role));
  }

}
