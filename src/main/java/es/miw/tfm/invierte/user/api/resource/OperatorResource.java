package es.miw.tfm.invierte.user.api.resource;

import es.miw.tfm.invierte.user.api.dto.OperatorDto;
import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.OperatorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing operator-related operations.
 * Provides endpoints for operator login, account creation,
 * password changes, and general information updates.
 *
 * <p>Accessible to users with roles ADMIN or SUPPORT.
 * Some endpoints are open to all authenticated users.
 *
 * @see OperatorService
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Log4j2
@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
@RequestMapping(OperatorResource.USERS)
@RequiredArgsConstructor
public class OperatorResource {

  public static final String USERS = "/users";

  public static final String TOKEN = "/token";

  public static final String OPERATOR = "/operator";

  public static final String EMAIL = "/{email}";

  public static final String CHANGE_PASSWORD = "/change-password";

  public static final String GENERAL_INFO = "/general-info";

  private final OperatorService operatorService;

  /**
   * Changes the password of an operator.
   *
   * @param email the email of the operator
   * @param passwordChangeDto the password change data transfer object
   */
  @PatchMapping(OPERATOR + EMAIL + CHANGE_PASSWORD)
  @PreAuthorize("permitAll()")
  public void changePassword(@PathVariable String email,
                             @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
    this.operatorService.changePassword(email, passwordChangeDto);
    log.info("Change password  email {}", email.replace("\n", "").replace("\r", ""));
  }

  /**
   * Creates a new operator account.
   *
   * @param operatorDto the operator data transfer object
   */
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping(OPERATOR)
  @PreAuthorize("hasRole('ADMIN')")
  public void createUserOperator(@Valid @RequestBody OperatorDto operatorDto) {
    this.operatorService.createUser(operatorDto.toOperator(), this.extractOperatorRoleClaims());
    log.info("Operator registered successfully: {}",
            operatorDto.getEmail().replace("\n", "").replace("\r", ""));
  }

  /**
   * Retrieves the general information of an operator.
   *
   * @param email the email of the operator
   * @return an OperatorInfoDto containing the operator's general information
   */
  @GetMapping(OPERATOR + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public OperatorInfoDto getOperatorInfoDto(@PathVariable String email) {
    return this.operatorService.readGeneralInfo(email);
  }

  /**
   * Logs in an operator and generates a token.
   *
   * @param activeUser the authenticated user
   * @return a TokenDto containing the generated token
   */
  @SecurityRequirement(name = "basicAuth")
  @PreAuthorize("authenticated")
  @PostMapping(value = OPERATOR + TOKEN)
  public TokenDto loginOperator(@AuthenticationPrincipal User activeUser) {
    TokenDto token = new TokenDto(operatorService.login(activeUser.getUsername()));
    log.debug(token::toString);
    return token;
  }

  /**
   * Updates the general information of an operator.
   *
   * @param email the email of the operator
   * @param operatorInfoDto the operator information data transfer object
   */
  @PatchMapping(OPERATOR + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public void updateGeneralInfo(@PathVariable String email,
                                @Valid @RequestBody OperatorInfoDto operatorInfoDto) {
    this.operatorService.updateGeneralInfo(email, operatorInfoDto);
    log.info("Updated general info email {}",
            email.replace("\n", "").replace("\r", ""));
  }

  private SystemRole extractOperatorRoleClaims() {
    return Stream.ofNullable(SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities())
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(GrantedAuthority::getAuthority)
        .map(SystemRole::of)
        .findFirst()
        .orElse(null);
  }

}
