package es.miw.tfm.invierte.user.api.resource;

import es.miw.tfm.invierte.user.api.dto.*;
import es.miw.tfm.invierte.user.service.StaffService;
import es.miw.tfm.invierte.user.service.util.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

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

  public static final String TAX_IDENTIFICATION_NUMBER = "/{taxIdentificationNumber}";

  public static final String CHANGE_PASSWORD = "/change-password";

  public static final String EMAIL = "/{email}";

  public static final String SET_COMPANY = "/set-company";

  public static final String NOTIFY_CODE = "/notify-code";

  public static final String ACTIVATE_CODE = "/activate-code/{activationCode}";

  public static final String GENERAL_INFO = "/general-info";

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
    log.info("Staff-No Company registered successfully: {}",
      staffDto.getEmail().replace("\n", "").replace("\r", ""));
  }

  @PatchMapping(STAFF + EMAIL + SET_COMPANY)
  @PreAuthorize("permitAll()")
  public void setCompanyToUser(@Valid @RequestBody StaffCompanyDto staffCompanyDto, @PathVariable String email) {
    this.staffService.setCompanyToUser(email, staffCompanyDto.getTaxIdentificationNumber());
    log.info("Company {} set to staff user {}",
      staffCompanyDto.getTaxIdentificationNumber().replace("\n", "").replace("\r", ""), email);
  }

  @PostMapping(STAFF + EMAIL + COMPANY + TAX_IDENTIFICATION_NUMBER + NOTIFY_CODE)
  @PreAuthorize("permitAll()")
  public void notify(@PathVariable String email, @PathVariable String taxIdentificationNumber) {
    this.staffService.getActivationCodeMessage(email, taxIdentificationNumber)
        .ifPresent(message -> this.emailService.sendEmail(email, "Unete a InvierteIO", message));
    log.info("Activation code - notification sent for  email {} - taxIdentificationNumber {}",
            email.replace("\n", "").replace("\r", ""),
            taxIdentificationNumber.replace("\n", "").replace("\r", ""));
  }

  @PostMapping(STAFF + ACTIVATE_CODE)
  @PreAuthorize("permitAll()")
  public void activateAccount(@PathVariable String activationCode) {
    this.staffService.activateAccount(activationCode);
    log.info("Activation code {} completed.", activationCode);
  }

  @PatchMapping(STAFF + EMAIL + CHANGE_PASSWORD)
  @PreAuthorize("permitAll()")
  public void changePassword(@PathVariable String email,
                             @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
    this.staffService.changePassword(email, passwordChangeDto);
    log.info("Change password  email {}", email.replace("\n", "").replace("\r", ""));
  }

  @PatchMapping(STAFF + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public void updateGeneralInfo(@PathVariable String email,
                             @Valid @RequestBody StaffInfoDto staffInfoDto) {
    this.staffService.updateGeneralInfo(email, staffInfoDto);
    log.info("Updated general info email {}", email.replace("\n", "").replace("\r", ""));
  }

  @GetMapping(STAFF + EMAIL + GENERAL_INFO)
  @PreAuthorize("permitAll()")
  public StaffInfoDto getGeneralInfo(@PathVariable String email) {
    return staffService.readGeneralInfo(email);
  }
}
