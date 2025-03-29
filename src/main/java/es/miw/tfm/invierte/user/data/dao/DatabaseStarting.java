package es.miw.tfm.invierte.user.data.dao;

import java.time.LocalDateTime;
import java.util.List;

import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class DatabaseStarting {

  private static final String SUPER_USER = "admin";

  private static final String EMAIL = "admin@invierte.io";

  private static final String PASSWORD = "6";

  private final OperatorRepository operatorRepository;

  @Autowired
  public DatabaseStarting(OperatorRepository operatorRepository) {
    this.operatorRepository = operatorRepository;
    this.initialize();
  }

  void initialize() {
    if (this.operatorRepository.findBySystemRoleIn(List.of(SystemRole.ADMIN)).isEmpty()) {
      Operator operator = new Operator();
      operator.setEmail(EMAIL);
      operator.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
      operator.setFirstName(SUPER_USER);
      operator.setRegistrationDate(LocalDateTime.now());
      operator.setSystemRole(SystemRole.ADMIN);

      this.operatorRepository.save(operator);
      log.warn("------- Created Admin -----------");
    }
  }

}
