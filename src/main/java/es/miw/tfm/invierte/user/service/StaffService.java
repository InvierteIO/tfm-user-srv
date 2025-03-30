package es.miw.tfm.invierte.user.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {

  private final StaffRepository staffRepository;

  private final UserRepository userRepository;

  private final JwtService jwtService;

  @Value("${message.activation-code.text}")
  private String messageText;

  @Value("${message.activation-code.base-url}")
  private String messageBaseUrl;

  private static ActivationCode generateActivationCode() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    return ActivationCode.builder()
        .code(UUID.randomUUID().toString())
        .expirationDate(currentDateTime.plusMinutes(30))
        .build();
  }

  public String login(String email) {

    final var user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found."));

    final var companyRoles = this.staffRepository.findByEmailAndStatus(email, Status.ACTIVE)
        .stream()
        .collect(Collectors.toMap(
            Staff::getTaxIdentificationNumber,
            staff -> staff.getCompanyRole().name()));

    return jwtService.createToken(user.getEmail(), user.getFirstName(), companyRoles);
  }

  public void createUserWithNoCompany(Staff staff) {
    staff.setDefaultNoCompany();
    this.assertNoExistByEmail(staff.getEmail());
    staff.setRegistrationDate(LocalDateTime.now());
    this.staffRepository.save(staff);
  }

  public void setCompanyToUser(String email, String taxIdentificationNumber) {
    this.assertUserIsInactiveAndHasNoCompany(email);
    this.staffRepository.findByEmailAndStatus(email, Status.INACTIVE)
        .stream()
        .filter(staff -> Objects.isNull(staff.getTaxIdentificationNumber()))
        .findFirst()
        .ifPresent(staff -> {
          staff.setTaxIdentificationNumber(taxIdentificationNumber);
          this.staffRepository.save(staff);
        });
  }

  public Optional<String> getActivationCodeMessage(String email, String taxIdentificationNumber) {
    this.assertStaffUserIsInactive(email, taxIdentificationNumber);
    return this.staffRepository.findByEmailAndTaxIdentificationNumber(email, taxIdentificationNumber)
        .stream()
        .filter(staff -> Status.INACTIVE.equals(staff.getStatus()))
        .findFirst()
        .map(staff -> {
          final var newActivationCode = generateActivationCode();
          staff.getActivationCodes().add(newActivationCode);
          this.staffRepository.save(staff);
          return newActivationCode;
        })
        .map(this::getActivationCodeBodyMessage);
  }

  public void activateAccount(String activationCode) {
    final var staffWithValidActivationCode = this.findStaffByActivationCode(activationCode)
        .orElseThrow(() -> new NotFoundException("Activation code not found or expired: " + activationCode));
    staffWithValidActivationCode.setStatus(Status.ACTIVE);
    this.staffRepository.save(staffWithValidActivationCode);
  }

  private String getActivationCodeBodyMessage(ActivationCode activationCode) {
    return String.format("%s %s", this.messageText, this.messageBaseUrl + "/" + activationCode.getCode());
  }

  private void assertUserIsInactiveAndHasNoCompany(String email) {
    final var inactiveStaffUser = this.staffRepository.findByEmailAndStatus(email, Status.INACTIVE)
        .stream()
        .filter(staff -> Objects.isNull(staff.getTaxIdentificationNumber()))
        .findFirst();

    if (inactiveStaffUser.isEmpty()) {
      throw new NotFoundException("There is no inactive user without company: " + email);
    }
  }

  private Optional<Staff> findStaffByActivationCode(String activationCode) {
    return this.staffRepository.findAll()
        .stream()
        .filter(staff -> staff.getActivationCodes()
            .stream()
            .anyMatch(code -> code.getCode().equals(activationCode)
                && code.getExpirationDate().isAfter(LocalDateTime.now())))
        .findFirst();
  }

  private void assertStaffUserIsInactive(String email, String taxIdentificationNumber) {
    this.staffRepository.findByEmailAndTaxIdentificationNumber(email, taxIdentificationNumber)
        .stream()
        .filter(staff -> Status.ACTIVE.equals(staff.getStatus()))
        .findFirst()
        .ifPresent(staff -> {
          throw new ConflictException("Relationship is not inactive: email " + email + " - taxIdentificationNumber " + taxIdentificationNumber);
        });
  }

  private void assertNoExistByEmail(String email) {
    this.staffRepository.findByEmail(email)
        .stream()
        .findFirst()
        .ifPresent(staff -> {
          throw new ConflictException("The email already exists: " + email + " - ");
        });
  }

}
