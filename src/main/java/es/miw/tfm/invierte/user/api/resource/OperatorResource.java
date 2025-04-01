package es.miw.tfm.invierte.user.api.resource;

import es.miw.tfm.invierte.user.api.dto.OperatorDto;
import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.OperatorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @PatchMapping(OPERATOR + EMAIL + CHANGE_PASSWORD)
  @PreAuthorize("permitAll()")
  public void changePassword(@PathVariable String email,
                             @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
    this.operatorService.changePassword(email, passwordChangeDto);
    log.info("Change password  email {}", email.replace("\n", "").replace("\r", ""));
  }

  @SecurityRequirement(name = "bearerAuth")
  @PostMapping(OPERATOR)
  @PreAuthorize("hasRole('ADMIN')")
  public void createUserOperator(@Valid @RequestBody OperatorDto operatorDto) {
    this.operatorService.createUser(operatorDto.toOperator(), this.extractOperatorRoleClaims());
    log.info("Operator registered successfully: {}",
            operatorDto.getEmail().replace("\n", "").replace("\r", ""));
  }

  @GetMapping(OPERATOR + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public OperatorInfoDto getOperatorInfoDto(@PathVariable String email) {
    return this.operatorService.readGeneralInfo(email);
  }

  @SecurityRequirement(name = "basicAuth")
  @PreAuthorize("authenticated")
  @PostMapping(value = OPERATOR + TOKEN)
  public TokenDto loginOperator(@AuthenticationPrincipal User activeUser) {
    TokenDto token = new TokenDto(operatorService.login(activeUser.getUsername()));
    log.debug(token::toString);
    return token;
  }

  @PatchMapping(OPERATOR + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public void updateGeneralInfo(@PathVariable String email,
                                @Valid @RequestBody OperatorInfoDto operatorInfoDto) {
    this.operatorService.updateGeneralInfo(email, operatorInfoDto);
    log.info("Updated general info email {}",
            email.replace("\n", "").replace("\r", ""));
  }

  private SystemRole extractOperatorRoleClaims() {
    List<String> roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).toList();
    return SystemRole.of(roleClaims.getFirst());
  }

}
