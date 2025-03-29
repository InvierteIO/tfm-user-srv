package es.miw.tfm.invierte.user.service;

import java.time.LocalDateTime;
import java.util.List;

import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperatorService {

  private final OperatorRepository operatorRepository;

  private final JwtService jwtService;

  private static List<SystemRole> authorizedRoles(SystemRole systemRole) {
    if (SystemRole.ADMIN.equals(systemRole)) {
      return List.of(SystemRole.ADMIN, SystemRole.SUPPORT);
    } else if (SystemRole.SUPPORT.equals(systemRole)) {
      return List.of(SystemRole.SUPPORT);
    } else {
      return List.of();
    }
  }

  public String login(String email) {
    return this.operatorRepository.findByEmail(email)
        .stream()
        .findFirst()
        .map(operator -> jwtService.createToken(operator.getEmail(), operator.getFirstName(), operator.getSystemRole().name()))
        .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
  }

  public void createUser(Operator operator, SystemRole systemRole) {
    if (!authorizedRoles(systemRole).contains(operator.getSystemRole())) {
      throw new ForbiddenException("Insufficient role to create this user: " + operator);
    }
    this.assertNoExistByEmail(operator.getEmail());
    operator.setRegistrationDate(LocalDateTime.now());
    this.operatorRepository.save(operator);
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
