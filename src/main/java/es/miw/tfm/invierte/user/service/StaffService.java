package es.miw.tfm.invierte.user.service;

import es.miw.tfm.invierte.user.api.dto.AccountConfirmationDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.api.dto.PasswordResetDto;
import es.miw.tfm.invierte.user.api.dto.StaffInfoDto;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import es.miw.tfm.invierte.user.service.exception.BadRequestException;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing staff users.
 * This class provides functionality for handling staff-related operations such as
 * login, account activation, password changes, and updating or reading general information.
 *
 * <p>Utilizes Spring's service layer and integrates with the `StaffRepository` and `UserRepository`
 * for database interactions.
 *
 * @see es.miw.tfm.invierte.user.data.dao.StaffRepository
 * @see es.miw.tfm.invierte.user.data.dao.UserRepository
 * @see es.miw.tfm.invierte.user.api.dto.StaffInfoDto
 * @see es.miw.tfm.invierte.user.api.dto.PasswordChangeDto
 * @see es.miw.tfm.invierte.user.service.exception.BadRequestException
 * @see es.miw.tfm.invierte.user.service.exception.ConflictException
 * @see es.miw.tfm.invierte.user.service.exception.NotFoundException
 * @see es.miw.tfm.invierte.user.data.model.enums.Status
 * @see es.miw.tfm.invierte.user.data.model.ActivationCode
 *
 * @author denilssonmn
 */
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

  @Value("${message.reset-password-code.text}")
  private String messageResetPasswordText;

  @Value("${message.reset-password-code.base-url}")
  private String messageResetPasswordBaseUrl;

  /**
   * Generates a new activation code with a 30-minute expiration time.
   *
   * @return the generated activation code
   */
  private static ActivationCode generateActivationCode() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    return ActivationCode.builder()
        .code(UUID.randomUUID().toString())
        .expirationDate(currentDateTime.plusMinutes(30))
        .build();
  }

  /**
   * Logs in a staff user and generates a JWT token.
   *
   * @param email the email of the staff user
   * @return the generated JWT token
   * @throws NotFoundException if the user is not found
   */
  public String login(String email) {

    final var user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found."));

    final var companyRoles = this.staffRepository.findByEmailAndStatus(email, Status.ACTIVE)
        .stream()
        .collect(Collectors.toMap(
            Staff::getTaxIdentificationNumber,
            staff -> staff.getCompanyRole().name()));

    return this.jwtService.createToken(user.getEmail(), user.getFirstName(), companyRoles);
  }

  /**
   * Creates a new staff user without a company.
   *
   * @param staff the staff user to be created
   * @throws ConflictException if the email already exists
   */
  public void createUserWithNoCompany(Staff staff) {
    staff.setDefaultNoCompany();
    this.assertNoExistByEmail(staff.getEmail());
    staff.setRegistrationDate(LocalDateTime.now());
    this.staffRepository.save(staff);
  }

  /**
   * Creates a new staff user associated to a Company.
   *
   * @param staff the staff user to be created
   * @throws ConflictException if the email already exists
   */
  public void createUserWithCompany(Staff staff) {
    staff.setNewCompanyUserDefault();
    this.assertNoExistByEmail(staff.getEmail());
    staff.setRegistrationDate(LocalDateTime.now());
    this.staffRepository.save(staff);
  }

  /**
   * Assigns a company to an inactive staff user without a company.
   *
   * @param email the email of the staff user
   * @param taxIdentificationNumber the tax identification number of the company
   * @throws NotFoundException if the user is not inactive or already has a company
   */
  public void setCompanyToUser(String email, String taxIdentificationNumber) {
    this.assertUserIsInactiveAndHasNoCompany(email);
    this.staffRepository.findByEmailAndStatus(email, Status.INACTIVE)
        .ifPresent(staff -> {
          staff.setTaxIdentificationNumber(taxIdentificationNumber);
          this.staffRepository.save(staff);
        });
  }

  /**
   * Retrieves the activation code message for a staff user.
   *
   * @param email the email of the staff user
   * @param taxIdentificationNumber the tax identification number of the company
   * @return an optional containing the activation code message, or empty if not found
   * @throws ConflictException if the user is not inactive
   */
  public Optional<String> getActivationCodeMessage(String email, String taxIdentificationNumber) {
    this.assertStaffUserIsInactive(email, taxIdentificationNumber);
    return this.staffRepository.findByEmailAndTaxIdentificationNumber(email,
            taxIdentificationNumber)
        .map(staff -> {
          final var newActivationCode = generateActivationCode();
          staff.getActivationCodes().add(newActivationCode);
          this.staffRepository.save(staff);
          return newActivationCode;
        })
        .map(this::getActivationCodeBodyMessage);
  }

  /**
   * Retrieves a reset password notification code message for a staff user.
   * Generates a new activation code, associates it with the staff user, and returns
   * the reset password notification message.
   *
   * @param email the email of the staff user
   * @return an optional containing the reset password notification message, or empty if not found
   * @throws ConflictException if the user is not active
   */
  public Optional<String> getResetPasswordNotificationCodeMessage(String email) {
    final var activeStaff = this.assertStaffUserIsActive(email);
    return Optional.of(activeStaff)
        .map(staff -> {
          final var newActivationCode = generateActivationCode();
          staff.getActivationCodes().add(newActivationCode);
          this.staffRepository.save(staff);
          return newActivationCode;
        })
        .map(this::getResetPasswordCodeBodyMessage);
  }


  /**
   * Activates a staff user's account using an activation code.
   *
   * @param activationCode the activation code
   * @throws NotFoundException if the activation code is not found or expired
   */
  public AccountConfirmationDto activateAccount(String activationCode) {
    return this.findStaffByNotificationCode(activationCode)
        .map(staff -> {
          staff.setStatus(Status.ACTIVE);
          this.staffRepository.save(staff);
          return new AccountConfirmationDto(!Objects.isNull(staff.getPassword()));
        })
        .orElseThrow(() ->
            new NotFoundException("Activation code not found or expired: " + activationCode));
  }

  /**
   * Changes the password of a staff user.
   *
   * @param email the email of the staff user
   * @param passwordChangeDto the DTO containing the old and new passwords
   * @throws BadRequestException if the old password does not match
   * @throws NotFoundException if the user is not found
   */
  public void changePassword(String email, PasswordChangeDto passwordChangeDto) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.staffRepository.findByEmailAndStatus(email, Status.ACTIVE)
        .map(op -> Optional.of(op)
          .filter(o -> passwordEncoder.matches(passwordChangeDto.getPassword(), o.getPassword()))
          .map(staffUpdated -> {
            staffUpdated.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            return staffUpdated;
          })
          .orElseThrow(() -> new BadRequestException("Passwords do not match with old password"))
        )
        .map(staffRepository::save)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }

  /**
   * Resets the password of a staff user.
   * Validates the provided notification token and ensures the user is active.
   * Updates the user's password with the new password provided in the DTO.
   *
   * @param email the email of the staff user
   * @param passwordResetDto the DTO containing the notification token and new password
   * @throws NotFoundException if the user is not found or the token is invalid
   */
  public void resetPassword(String email, PasswordResetDto passwordResetDto) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.findStaffByNotificationCode(passwordResetDto.getNotificationToken())
        .filter(staff ->  Status.ACTIVE.equals(staff.getStatus())
            && staff.getEmail().equals(email))
        .map(staff -> {
          staff.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
          return staff;
        })
        .map(staffRepository::save)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }

  /**
   * Updates the general information of a staff user.
   *
   * @param email the email of the staff user
   * @param staffInfoDto the DTO containing the updated information
   * @throws NotFoundException if the staff user is not found
   */
  public void updateGeneralInfo(String email, StaffInfoDto staffInfoDto) {
    this.staffRepository.findByEmailAndStatus(email, Status.ACTIVE)
        .map(staff -> {
          BeanUtils.copyProperties(staffInfoDto, staff);
          return staff;
        }).map(staffRepository::save)
      .orElseThrow(() -> new NotFoundException("Staff not found"));
  }

  /**
   * Reads the general information of a staff user.
   *
   * @param email the email of the staff user
   * @return the DTO containing the staff user's general information
   * @throws NotFoundException if the staff user is not found
   */
  public StaffInfoDto readGeneralInfo(String email) {
    return this.staffRepository.findByEmailAndStatus(email, Status.ACTIVE)
      .map(staff -> {
        StaffInfoDto staffInfoDto = new StaffInfoDto();
        BeanUtils.copyProperties(staff, staffInfoDto);
        return staffInfoDto;
      }).orElseThrow(() -> new NotFoundException("Staff not found"));
  }

  /**
   * Generates the body message for an activation code.
   *
   * @param activationCode the activation code
   * @return the activation code body message
   */
  private String getActivationCodeBodyMessage(ActivationCode activationCode) {
    return String.format("%s %s", this.messageText, this.messageBaseUrl + "/"
        + activationCode.getCode());
  }

  private String getResetPasswordCodeBodyMessage(ActivationCode activationCode) {
    return String.format("%s %s", this.messageResetPasswordText,
        this.messageResetPasswordBaseUrl + "/"
        + activationCode.getCode());
  }

  /**
   * Asserts that a user is inactive and has no company assigned.
   *
   * @param email the email of the staff user
   * @throws NotFoundException if the user is not inactive or already has a company
   */
  private void assertUserIsInactiveAndHasNoCompany(String email) {
    final var inactiveStaffUser = this.staffRepository.findByEmailAndStatus(email, Status.INACTIVE)
        .filter(staff -> Objects.isNull(staff.getTaxIdentificationNumber()));

    if (inactiveStaffUser.isEmpty()) {
      throw new NotFoundException("There is no inactive user without company: " + email);
    }
  }

  /**
   * Finds a staff user by activation code.
   *
   * @param notificationCode the notification code
   * @return an optional containing the staff user, or empty if not found
   */
  private Optional<Staff> findStaffByNotificationCode(String notificationCode) {
    return this.staffRepository.findAll()
        .stream()
        .filter(staff -> staff.getActivationCodes()
            .stream()
            .anyMatch(code -> code.getCode().equals(notificationCode)
                && code.getExpirationDate().isAfter(LocalDateTime.now())))
        .findFirst();
  }

  /**
   * Asserts that a staff user is inactive for a specific company.
   *
   * @param email the email of the staff user
   * @param taxIdentificationNumber the tax identification number of the company
   * @throws ConflictException if the user is active
   */
  private void assertStaffUserIsInactive(String email, String taxIdentificationNumber) {
    this.staffRepository.findByEmailAndTaxIdentificationNumber(email, taxIdentificationNumber)
        .filter(staff -> Status.ACTIVE.equals(staff.getStatus()))
        .ifPresent(staff -> {
          throw new ConflictException("Relationship is not inactive: email " + email
              + " - taxIdentificationNumber " + taxIdentificationNumber);
        });
  }

  private Staff assertStaffUserIsActive(String email) {
    return this.staffRepository.findByEmail(email)
        .filter(staff -> Status.ACTIVE.equals(staff.getStatus()))
        .orElseThrow(() -> new ConflictException("User is not active: email " + email));
  }


  /**
   * Asserts that no staff user exists with the given email.
   *
   * @param email the email to check
   * @throws ConflictException if a staff user with the email already exists
   */
  private void assertNoExistByEmail(String email) {
    this.staffRepository.findByEmail(email)
        .stream()
        .findFirst()
        .ifPresent(staff -> {
          throw new ConflictException("The email already exists: " + email + " - ");
        });
  }

}
