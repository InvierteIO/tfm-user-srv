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


@Service
@RequiredArgsConstructor
public class OperatorService {

  private final OperatorRepository operatorRepository;

  private final JwtService jwtService;

    public static final String OPERATOR_NOT_FOUND = "Operator not found";

  private static List<SystemRole> authorizedRoles(SystemRole systemRole) {
    if (SystemRole.ADMIN.equals(systemRole)) {
      return List.of(SystemRole.ADMIN, SystemRole.SUPPORT);
    } else if (SystemRole.SUPPORT.equals(systemRole)) {
      return List.of(SystemRole.SUPPORT);
    } else {
      return List.of();
    }
  }

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

  public void createUser(Operator operator, SystemRole systemRole) {
    if (!authorizedRoles(systemRole).contains(operator.getSystemRole())) {
      throw new ForbiddenException("Insufficient role to create this user: " + operator);
    }
    this.assertNoExistByEmail(operator.getEmail());
    operator.setRegistrationDate(LocalDateTime.now());
    this.operatorRepository.save(operator);
  }

  public String login(String email) {
    return this.operatorRepository.findByEmail(email)
       .stream()
       .findFirst()
       .map(operator -> jwtService.createToken(operator.getEmail(), operator.getFirstName(), operator.getSystemRole().name()))
       .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
  }

  public void updateGeneralInfo(String email, OperatorInfoDto operatorInfoDto) {
    this.operatorRepository.findByEmail(email)
        .map(operator -> {
            BeanUtils.copyProperties(operatorInfoDto, operator);
            return operator;
        })
        .map(operatorRepository::save)
        .orElseThrow(() -> new NotFoundException(OPERATOR_NOT_FOUND));
  }

  public OperatorInfoDto readGeneralInfo(String email) {
      return this.operatorRepository.findByEmail(email)
          .map(operator -> {
              OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
              BeanUtils.copyProperties(operator, operatorInfoDto);
              return operatorInfoDto;
          })
          .orElseThrow(() -> new NotFoundException(OPERATOR_NOT_FOUND));
  }

  private void assertNoExistByEmail(String email) {
    this.operatorRepository.findByEmail(email)
        .stream()
        .findFirst()
        .ifPresent(operator -> {
          throw new ConflictException("The email already exists: " + email);
        });
  }
}
