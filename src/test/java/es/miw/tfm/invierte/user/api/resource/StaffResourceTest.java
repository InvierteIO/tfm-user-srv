package es.miw.tfm.invierte.user.api.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import es.miw.tfm.invierte.user.api.dto.AccountConfirmationDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.api.dto.PasswordResetDto;
import es.miw.tfm.invierte.user.api.dto.StaffCompanyDto;
import es.miw.tfm.invierte.user.api.dto.StaffDto;
import es.miw.tfm.invierte.user.api.dto.StaffInfoDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.service.StaffService;
import es.miw.tfm.invierte.user.service.util.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

@ExtendWith({MockitoExtension.class})
public class StaffResourceTest {

  @Mock
  private StaffService staffService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private StaffResource staffResource;

  public static final String EMAIL = "test@example.com";


  @Test
  void testLoginStaff() {
    String token = "mockedToken";
    User activeUser = new User(EMAIL, "password", List.of());
    when(this.staffService.login(EMAIL)).thenReturn(token);

    TokenDto result = this.staffResource.loginStaff(activeUser);

    assertNotNull(result);
    assertEquals(token, result.getToken());
    verify(this.staffService).login(EMAIL);
  }

  @Test
  void testCreateUserWithNoCompany() {
    StaffDto staffDto = buildStaffDto();
    doNothing().when(this.staffService).createUserWithNoCompany(staffDto.toStaff());

    this.staffResource.createUserWithNoCompany(staffDto);

    verify(this.staffService).createUserWithNoCompany(staffDto.toStaff());
  }

  @Test
  void testSetCompanyToUser() {
    String email = "test@example.com";
    StaffCompanyDto staffCompanyDto = new StaffCompanyDto("12345678A");

    doNothing().when(this.staffService).setCompanyToUser(email, staffCompanyDto.getTaxIdentificationNumber());

    this.staffResource.setCompanyToUser(staffCompanyDto, email);

    verify(this.staffService).setCompanyToUser(email, staffCompanyDto.getTaxIdentificationNumber());
  }

  @Test
  void testNotify() {
    String email = "test@example.com";
    String taxIdentificationNumber = "12345678A";
    String message = "Activation code message";
    String subject = "Unete a InvierteIO";

    when(this.staffService.getActivationCodeMessage(email, taxIdentificationNumber)).thenReturn(java.util.Optional.of(message));
    doNothing().when(this.emailService).sendEmail(email, subject, message);

    this.staffResource.notifyActivationCode(email, taxIdentificationNumber);

    verify(this.staffService).getActivationCodeMessage(email, taxIdentificationNumber);
    verify(this.emailService).sendEmail(email, subject, message);
  }

  @Test
  void testActivateAccount() {
    String activationCode = "activationCode";

    when(this.staffService.activateAccount(activationCode)).thenReturn(new AccountConfirmationDto(true));

    final var actualResponse = this.staffResource.activateAccount(activationCode);

    verify(this.staffService).activateAccount(activationCode);
    assertNotNull(actualResponse);
    assertTrue(actualResponse.isPasswordSet());
  }

  @Test
  void testChangePassword() {
    String email = "test@example.com";
    PasswordChangeDto passwordChangeDto = new PasswordChangeDto("oldPassword", "newPassword");

    doNothing().when(this.staffService).changePassword(email, passwordChangeDto);

    this.staffResource.changePassword(email, passwordChangeDto);

    verify(this.staffService).changePassword(email, passwordChangeDto);
  }

  @Test
  void testUpdateGeneralInfo() {
    String email = "test@example.com";
    StaffInfoDto staffInfoDto = new StaffInfoDto();

    doNothing().when(this.staffService).updateGeneralInfo(email, staffInfoDto);

    this.staffResource.updateGeneralInfo(email, staffInfoDto);

    verify(this.staffService).updateGeneralInfo(email, staffInfoDto);
  }

  @Test
  void testGetGeneralInfo() {
    String email = "test@example.com";
    StaffInfoDto staffInfoDto = new StaffInfoDto();

    when(this.staffService.readGeneralInfo(email)).thenReturn(staffInfoDto);

    StaffInfoDto result = this.staffResource.getGeneralInfo(email);

    assertNotNull(result);
    assertEquals(staffInfoDto.getFirstName(), result.getFirstName());
    assertEquals(staffInfoDto.getFamilyName(), result.getFamilyName());
    verify(this.staffService).readGeneralInfo(email);
  }

  @Test
  void testCreateUserWithCompany() {
    StaffDto staff = buildStaffDto();

    String taxIdentificationNumber = "123456789";
    doNothing().when(this.staffService).createUserWithCompany(staff.toStaff());
    this.staffResource.createUserWithCompany(staff, taxIdentificationNumber);
    verify(staffService, times(1)).createUserWithCompany(staff.toStaff());
  }

  @Test
  void testNotifyResetPassword() {
    String email = "test@example.com";
    this.staffResource.notifyResetPassword(email);
    verify(staffService, times(1)).getResetPasswordNotificationCodeMessage(email);
  }

  @Test
  void testResetPassword() {
    String email = "test@example.com";
    PasswordResetDto passwordResetDto = new PasswordResetDto();
    staffResource.resetPassword(email, passwordResetDto);
    verify(staffService, times(1)).resetPassword(email, passwordResetDto);
  }

  private static StaffDto buildStaffDto() {
    return StaffDto.builder()
        .email(EMAIL)
        .password("password")
        .familyName("Test")
        .firstName("User")
        .companyRole(CompanyRole.OWNER)
        .build();
  }


}
