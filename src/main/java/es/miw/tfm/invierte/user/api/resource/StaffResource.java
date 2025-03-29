package es.miw.tfm.invierte.user.api.resource;

import es.miw.tfm.invierte.user.api.dto.StaffCompanyDto;
import es.miw.tfm.invierte.user.api.dto.StaffDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.service.StaffService;
import es.miw.tfm.invierte.user.service.util.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(StaffResource.USERS)
@PreAuthorize("hasRole('OWNER') or hasRole('AGENT')")
@RequiredArgsConstructor
public class StaffResource {

  public static final String USERS = "/users";

  public static final String TOKEN = "/token";

  public static final String STAFF = "/staff";

  public static final String NO_COMPANY = "/no-company";

  public static final String COMPANY = "/companies";

  public static final String TAX_IDENTIFIER_NUMBER = "/{taxIdentifierNumber}";

  public static final String EMAIL = "/{email}";

  public static final String SET_COMPANY = "/set-company";

  public static final String NOTIFY_CODE = "/notify-code";

  public static final String ACTIVATE_CODE = "/activate-code/{activationCode}";

  private final StaffService staffService;

  private final EmailService emailService;

  @SecurityRequirement(name = "basicAuth")
  @PreAuthorize("authenticated")
  @PostMapping(value = STAFF + TOKEN)
  public TokenDto loginStaff(@AuthenticationPrincipal User activeUser) {
    TokenDto token = new TokenDto(staffService.login(activeUser.getUsername()));
    log.debug(token::toString);
    return token;
  }

  @PostMapping(STAFF + NO_COMPANY)
  @PreAuthorize("permitAll()")
  public void createUserWithNoCompany(@Valid @RequestBody StaffDto staffDto) {
    this.staffService.createUserWithNoCompany(staffDto.toStaff());
    log.info("Staff-No Company registered successfully: {}", staffDto.getEmail());
  }

  @PatchMapping(STAFF + EMAIL + SET_COMPANY)
  @PreAuthorize("permitAll()")
  public void setCompanyToUser(@Valid @RequestBody StaffCompanyDto staffCompanyDto, @PathVariable String email) {
    this.staffService.setCompanyToUser(email, staffCompanyDto.getTaxIdentifierNumber());
    log.info("Company {} set to staff user {}", staffCompanyDto.getTaxIdentifierNumber(), email);
  }

  @PostMapping(STAFF + EMAIL + COMPANY + TAX_IDENTIFIER_NUMBER + NOTIFY_CODE)
  @PreAuthorize("permitAll()")
  public void notify(@PathVariable String email, @PathVariable String taxIdentifierNumber) {
    this.staffService.getActivationCodeMessage(email, taxIdentifierNumber)
        .ifPresent(message -> this.emailService.sendEmail(email, "Unete a InvierteIO", message));
    log.info("Activation code - notification sent for  email {} - taxIdentifierNumber {}", email, taxIdentifierNumber);
  }

  @PostMapping(STAFF + ACTIVATE_CODE)
  @PreAuthorize("permitAll()")
  public void activateAccount(@PathVariable String activationCode) {
    this.staffService.activateAccount(activationCode);
    log.info("Activation code {} completed.", activationCode);
  }

}
