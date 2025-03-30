package es.miw.tfm.invierte.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class StaffServiceTest {

  private static final String TOKEN = "token";

  private static final String EMAIL = "email@email.com";

  private static final String NAME = "test";

  @InjectMocks
  private StaffService staffService;

  @Mock
  private StaffRepository staffRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtService jwtService;

  private Staff staff;

  private static Staff buildInactiveStaff() {
    Staff staff = new Staff();
    staff.setEmail(EMAIL);
    staff.setFirstName(NAME);
    staff.setCompanyRole(CompanyRole.AGENT);
    staff.setTaxIdentificationNumber("123456");
    staff.setStatus(Status.INACTIVE);
    return staff;
  }

  private static Staff buildInactiveStaffWithNoCompany() {
    Staff staff = new Staff();
    staff.setEmail(EMAIL);
    staff.setFirstName(NAME);
    staff.setCompanyRole(CompanyRole.AGENT);
    staff.setStatus(Status.INACTIVE);
    return staff;
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
}
