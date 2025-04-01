package es.miw.tfm.invierte.user.service;

import static es.miw.tfm.invierte.user.util.DummyStaffUtil.EMAIL;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.NAME;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.TOKEN;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildInactiveStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.buildInactiveStaffWithNoCompany;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomPasswordChangeDto;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaffInfoDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.Mock;
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

  @Test
  void testActivateAccount() {
    final var mockedInactiveStaff = buildInactiveStaff();
    ActivationCode activationCode = new ActivationCode();
    activationCode.setCode(UUID.randomUUID().toString());
    activationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
    mockedInactiveStaff.getActivationCodes().add(activationCode);

    when(this.staffRepository.findAll()).thenReturn(List.of(mockedInactiveStaff));
    staffService.activateAccount(activationCode.getCode());
    verify(this.staffRepository).save(any(Staff.class));
  }

  @Test
  void testChangePasswordWhenIsOk() {
    staff = createRandomStaff(Status.ACTIVE);
    when(staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
            .thenReturn(List.of(staff));
    final var passwordChangeDto = createRandomPasswordChangeDto();
    var staffChangePass = createRandomStaff(Status.ACTIVE);
    staffChangePass.setPassword(new BCryptPasswordEncoder()
            .encode(passwordChangeDto.getNewPassword()));
    when(staffRepository.save(isA(Staff.class))).thenReturn(staffChangePass);

    assertDoesNotThrow(() ->
      staffService.changePassword(staff.getEmail(), passwordChangeDto));
  }

  @Test
  void testCreateUserWithNoCompany() {
    final var mockedStaff = buildInactiveStaffWithNoCompany();
    when(this.staffRepository.findByEmail(EMAIL)).thenReturn(List.of());
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
  void testLoginSuccess() {
    final var mockedStaff = buildInactiveStaff();
    when(this.userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockedStaff));
    when(this.staffRepository.findByEmailAndStatus(EMAIL, Status.ACTIVE)).thenReturn(List.of(mockedStaff));
    when(this.jwtService.createToken(anyString(), anyString(), anyMap())).thenReturn(TOKEN);

    String actualToken = staffService.login(EMAIL);

    verify(this.userRepository).findByEmail(EMAIL);
    verify(this.jwtService).createToken(eq(EMAIL), eq(NAME), anyMap());
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
    when(this.staffRepository.findByEmailAndStatus(EMAIL, Status.INACTIVE)).thenReturn(List.of(mockedInactiveStaffWithNoCompany));
    this.staffService.setCompanyToUser(EMAIL, "123456");
    verify(this.staffRepository).save(any(Staff.class));
  }

  @Test
  void testGetActivationCodeMessage() {
    final var mockedInactiveStaff = buildInactiveStaff();
    when(this.staffRepository.findByEmailAndTaxIdentificationNumber(EMAIL, "123456")).thenReturn(List.of(mockedInactiveStaff));
    when(this.staffRepository.save(any(Staff.class))).thenReturn(staff);
    Optional<String> message = staffService.getActivationCodeMessage(EMAIL, "123456");
    verify(this.staffRepository).save(any(Staff.class));
    assertTrue(message.isPresent());
  }

  @Test
  void testUpdateGeneralInfoWhenIsOk() {
    var staffActive = createRandomStaff(Status.ACTIVE);
    when(staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(List.of(staffActive));
    when(staffRepository.save(staffCaptor.capture())).thenReturn(staffActive);
    var staffInfoDto = createRandomStaffInfoDto();
    staffService.updateGeneralInfo(EMAIL, staffInfoDto);
    assertEquals(staffActive.getFirstName(), staffCaptor.getValue().getFirstName());
  }

  @Test
  void testUpdateGeneralInfoWhenIsNotFound() {
    when(staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(List.of());
    var staffInfoDto = createRandomStaffInfoDto();
    assertThrows(NotFoundException.class, () ->
      staffService.updateGeneralInfo(EMAIL, staffInfoDto));
  }

  @Test
  void testReadGeneralInfoWhenIsOk() {
    var staffActive = createRandomStaff(Status.ACTIVE);
    when(staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(List.of(staffActive));
    var infoDto = staffService.readGeneralInfo(EMAIL);
    assertEquals(staffActive.getFirstName(), infoDto.getFirstName());
    verify(staffRepository).findByEmailAndStatus(EMAIL, Status.ACTIVE);
  }

  @Test
  void testReadGeneralInfoWhenIsNotFound() {
    when(staffRepository.findByEmailAndStatus(anyString(), isA(Status.class)))
      .thenReturn(List.of());
    assertThrows(NotFoundException.class, () ->
            staffService.readGeneralInfo(EMAIL));
  }
}
