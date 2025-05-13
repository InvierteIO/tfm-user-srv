package es.miw.tfm.invierte.user.service;

import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.exception.BadRequestException;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing operators.
 * This class provides functionality for handling operator-related operations such as
 * password changes, user creation, login, and updating or reading general information.
 *
 * <p>Utilizes Spring's service layer and integrates with the `OperatorRepository` for
 * database interactions.
 *
 * @see es.miw.tfm.invierte.user.data.dao.OperatorRepository
 * @see es.miw.tfm.invierte.user.api.dto.OperatorInfoDto
 * @see es.miw.tfm.invierte.user.api.dto.PasswordChangeDto
 * @see es.miw.tfm.invierte.user.service.exception.BadRequestException
 * @see es.miw.tfm.invierte.user.service.exception.ConflictException
 * @see es.miw.tfm.invierte.user.service.exception.ForbiddenException
 * @see es.miw.tfm.invierte.user.service.exception.NotFoundException
 *
 * @author denilssonmn
 */
@Service
@RequiredArgsConstructor
public class OperatorService {

  private final OperatorRepository operatorRepository;

  private final JwtService jwtService;

  public static final String OPERATOR_NOT_FOUND = "Operator not found";

  /**
   * Retrieves the list of authorized roles based on the given system role.
   *
   * @param systemRole the system role of the operator
   * @return a list of authorized roles
   */
  private static List<SystemRole> authorizedRoles(SystemRole systemRole) {
    if (SystemRole.ADMIN.equals(systemRole)) {
      return List.of(SystemRole.ADMIN, SystemRole.SUPPORT);
    } else if (SystemRole.SUPPORT.equals(systemRole)) {
      return List.of(SystemRole.SUPPORT);
    } else {
      return List.of();
    }
  }

  /**
   * Changes the password of an operator.
   *
   * @param email the email of the operator
   * @param passwordChangeDto the DTO containing the old and new passwords
   * @throws BadRequestException if the old password does not match
   * @throws NotFoundException if the operator is not found
   */
  public void changePassword(String email, PasswordChangeDto passwordChangeDto) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.operatorRepository.findByEmail(email)
      .map(op ->
        Optional.of(op)
          .filter(o -> passwordEncoder.matches(passwordChangeDto.getPassword(), o.getPassword()))
          .map(o -> {
            o.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            return this.operatorRepository.save(o);
          })
          .orElseThrow(() -> new BadRequestException("Passwords do not match with old password"))
      ).orElseThrow(() -> new NotFoundException(OPERATOR_NOT_FOUND));
  }

  /**
   * Creates a new operator user.
   *
   * @param operator the operator to be created
   * @param systemRole the system role of the operator performing the action
   * @throws ForbiddenException if the system role is insufficient
   * @throws ConflictException if the email already exists
   */
  public void createUser(Operator operator, SystemRole systemRole) {
    if (!authorizedRoles(systemRole).contains(operator.getSystemRole())) {
      throw new ForbiddenException("Insufficient role to create this user: " + operator);
    }
    this.assertNoExistByEmail(operator.getEmail());
    operator.setRegistrationDate(LocalDateTime.now());
    this.operatorRepository.save(operator);
  }

  /**
   * Logs in an operator and generates a JWT token.
   *
   * @param email the email of the operator
   * @return the generated JWT token
   * @throws NotFoundException if the operator is not found
   */
  public String login(String email) {
    return this.operatorRepository.findByEmail(email)
       .stream()
       .findFirst()
       .map(operator ->
           jwtService.createToken(operator.getEmail(),
               operator.getFirstName(),
               operator.getSystemRole().name()))
       .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
  }

  /**
   * Updates the general information of an operator.
   *
   * @param email the email of the operator
   * @param operatorInfoDto the DTO containing the updated information
   * @throws NotFoundException if the operator is not found
   */
  public void updateGeneralInfo(String email, OperatorInfoDto operatorInfoDto) {
    this.operatorRepository.findByEmail(email)
        .map(operator -> {
          BeanUtils.copyProperties(operatorInfoDto, operator);
          return operator;
        })
        .map(operatorRepository::save)
        .orElseThrow(() -> new NotFoundException(OPERATOR_NOT_FOUND));
  }

  /**
   * Reads the general information of an operator.
   *
   * @param email the email of the operator
   * @return the DTO containing the operator's general information
   * @throws NotFoundException if the operator is not found
   */
  public OperatorInfoDto readGeneralInfo(String email) {
    return this.operatorRepository.findByEmail(email)
          .map(operator -> {
            OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
            BeanUtils.copyProperties(operator, operatorInfoDto);
            return operatorInfoDto;
          })
          .orElseThrow(() -> new NotFoundException(OPERATOR_NOT_FOUND));
  }

  /**
   * Asserts that no operator exists with the given email.
   *
   * @param email the email to check
   * @throws ConflictException if an operator with the email already exists
   */
  private void assertNoExistByEmail(String email) {
    this.operatorRepository.findByEmail(email)
        .stream()
        .findFirst()
        .ifPresent(operator -> {
          throw new ConflictException("The email already exists: " + email);
        });
  }
}
