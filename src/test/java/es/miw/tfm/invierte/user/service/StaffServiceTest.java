package es.miw.tfm.invierte.user.service;

import static es.miw.tfm.invierte.user.util.DummyStaffUtil.EMAIL;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.NAME;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.TOKEN;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildActiveStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildActiveStaffWithNoCompany;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildInactiveStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildInactiveStaffWithNoCompany;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomPasswordChangeDto;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaffInfoDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith({MockitoExtension.class})
class StaffServiceTest {

  @InjectMocks
  private StaffService staffService;

  @Mock
  private StaffRepository staffRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtService jwtService;

  @Captor
  ArgumentCaptor<Staff> staffCaptor;

  @Spy
  private Staff staff;

  private static final String TAX_IDENTIFICATION_NUMBER = "123456";

  @Test
  void testActivateAccount() {
    final var mockedInactiveStaff = buildInactiveStaff();
    ActivationCode activationCode = new ActivationCode();
    activationCode.setCode(UUID.randomUUID().toString());
    activationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
    mockedInactiveStaff.getActivationCodes().add(activationCode);

    when(this.staffRepository.findAll()).thenReturn(List.of(mockedInactiveStaff));
    this.staffService.activateAccount(activationCode.getCode());
    verify(this.staffRepository).save(argThat(staffSave -> Status.ACTIVE.equals(staffSave.getStatus())));
  }

  @Test
  void testActivateAccountNotFound() {
    final var mockedInactiveStaff = buildInactiveStaff();
    ActivationCode activationCode = new ActivationCode();
    activationCode.setCode(UUID.randomUUID().toString());
    activationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
    mockedInactiveStaff.getActivationCodes().add(activationCode);

    when(this.staffRepository.findAll()).thenReturn(List.of(mockedInactiveStaff));
    String nonValidActivationCode = UUID.randomUUID().toString();
    assertThrows(NotFoundException.class, ()->this.staffService.activateAccount(nonValidActivationCode));

    verify(this.staffRepository, never()).save(any());
  }

  @Test
  void testChangePasswordWhenIsOk() {
    staff = createRandomStaff(Status.ACTIVE);
    when(this.staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
            .thenReturn(Optional.of(staff));
    final var passwordChangeDto = createRandomPasswordChangeDto();
    var staffChangePass = createRandomStaff(Status.ACTIVE);
    staffChangePass.setPassword(new BCryptPasswordEncoder()
            .encode(passwordChangeDto.getNewPassword()));
    when(this.staffRepository.save(isA(Staff.class))).thenReturn(staffChangePass);

    assertDoesNotThrow(() ->
      this.staffService.changePassword(staff.getEmail(), passwordChangeDto));
    verify(this.staffRepository).save(any());
  }

  @Test
  void testCreateUserWithNoCompany() {
    final var mockedStaff = buildInactiveStaffWithNoCompany();
    when(this.staffRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    this.staffService.createUserWithNoCompany(mockedStaff);
    verify(this.staffRepository).save(argThat(newStaff ->
        newStaff.getEmail().equals(EMAIL)
            && newStaff.getFirstName().equals(NAME)
            && newStaff.getStatus().equals(Status.INACTIVE)
            && newStaff.getRegistrationDate() != null
            && newStaff.getTaxIdentificationNumber() == null
            && newStaff.getCompanyRole().equals(CompanyRole.OWNER)
            && newStaff.getActivationCodes().isEmpty()
    ));
  }

  @Test
  void testCreateUserWithNoCompanyUserIsActive() {
    final var mockedStaff = buildActiveStaffWithNoCompany();
    when(this.staffRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockedStaff));
    assertThrows(ConflictException.class,() ->this.staffService.createUserWithNoCompany(mockedStaff));
  }

  @Test
  void testLoginSuccess() {
    final var mockedStaff = buildInactiveStaff();
    final var mockedStaffRoles = new HashMap<String, String>();
    mockedStaffRoles.put(mockedStaff.getTaxIdentificationNumber(), mockedStaff.getCompanyRole().name());

    when(this.userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockedStaff));
    when(this.staffRepository.findByEmailAndStatus(EMAIL, Status.ACTIVE)).thenReturn(Optional.of(mockedStaff));
    when(this.jwtService.createToken(EMAIL, NAME, mockedStaffRoles)).thenReturn(TOKEN);

    String actualToken = this.staffService.login(EMAIL);

    verify(this.userRepository).findByEmail(EMAIL);
    verify(this.jwtService).createToken(EMAIL, NAME, mockedStaffRoles);
    assertEquals(TOKEN, actualToken);
  }

  @Test
  void testLoginNotFound() {
    when(this.userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> this.staffService.login(EMAIL));
  }

  @Test
  void testSetCompanyToUser() {
    final var mockedInactiveStaffWithNoCompany = buildInactiveStaffWithNoCompany();
    when(this.staffRepository.findByEmailAndStatus(EMAIL, Status.INACTIVE)).thenReturn(Optional.of(mockedInactiveStaffWithNoCompany));

    this.staffService.setCompanyToUser(EMAIL, TAX_IDENTIFICATION_NUMBER);

    verify(this.staffRepository, times(2)).findByEmailAndStatus(EMAIL, Status.INACTIVE);
    verify(this.staffRepository).save(argThat(newStaff ->
        newStaff.getEmail().equals(EMAIL)
            && newStaff.getStatus().equals(Status.INACTIVE)
            && newStaff.getTaxIdentificationNumber().equals(TAX_IDENTIFICATION_NUMBER)
    ));

  }

  @Test
  void testGetActivationCodeMessage() {
    final var mockedInactiveStaff = buildInactiveStaff();
    when(this.staffRepository.findByEmailAndTaxIdentificationNumber(EMAIL, TAX_IDENTIFICATION_NUMBER))
        .thenReturn(Optional.of(mockedInactiveStaff));
    when(this.staffRepository.save(any(Staff.class))).thenReturn(staff);

    Optional<String> message = this.staffService.getActivationCodeMessage(EMAIL, TAX_IDENTIFICATION_NUMBER);

    verify(this.staffRepository, times(2))
        .findByEmailAndTaxIdentificationNumber(EMAIL, TAX_IDENTIFICATION_NUMBER);
    verify(this.staffRepository).save(any(Staff.class));
    assertTrue(message.isPresent());
  }

  @Test
  void testGetActivationCodeMessageUserIsActive() {
    final var mockedActiveStaff = buildActiveStaff();
    when(this.staffRepository.findByEmailAndTaxIdentificationNumber(EMAIL, TAX_IDENTIFICATION_NUMBER))
        .thenReturn(Optional.of(mockedActiveStaff));

    assertThrows(ConflictException.class, ()-> this.staffService.getActivationCodeMessage(EMAIL, TAX_IDENTIFICATION_NUMBER));

    verify(this.staffRepository, times(1))
        .findByEmailAndTaxIdentificationNumber(EMAIL, TAX_IDENTIFICATION_NUMBER);


  }

  @Test
  void testUpdateGeneralInfoWhenIsOk() {
    var staffActive = createRandomStaff(Status.ACTIVE);
    when(this.staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(Optional.of(staffActive));
    when(this.staffRepository.save(this.staffCaptor.capture())).thenReturn(staffActive);
    
    var staffInfoDto = createRandomStaffInfoDto();
    
    this.staffService.updateGeneralInfo(EMAIL, staffInfoDto);
    assertEquals(staffActive.getFirstName(), this.staffCaptor.getValue().getFirstName());
    verify(this.staffRepository).save(argThat(newStaff ->
        newStaff.getEmail().equals(EMAIL)
            && newStaff.getFirstName().equals(staffInfoDto.getFirstName())
            && newStaff.getFamilyName().equals(staffInfoDto.getFamilyName())
            && Gender.MALE.equals(newStaff.getGender())
    ));
  }

  @Test
  void testUpdateGeneralInfoWhenIsNotFound() {
    when(this.staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(Optional.empty());
    var staffInfoDto = createRandomStaffInfoDto();
    assertThrows(NotFoundException.class, () ->
      this.staffService.updateGeneralInfo(EMAIL, staffInfoDto));
  }

  @Test
  void testReadGeneralInfoWhenIsOk() {
    var staffActive = createRandomStaff(Status.ACTIVE);
    when(this.staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(Optional.of(staffActive));
    var infoDto = this.staffService.readGeneralInfo(EMAIL);
    assertEquals(staffActive.getFirstName(), infoDto.getFirstName());
    verify(staffRepository).findByEmailAndStatus(EMAIL, Status.ACTIVE);
  }

  @Test
  void testReadGeneralInfoWhenIsNotFound() {
    when(this.staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () ->
            this.staffService.readGeneralInfo(EMAIL));
  }
}
