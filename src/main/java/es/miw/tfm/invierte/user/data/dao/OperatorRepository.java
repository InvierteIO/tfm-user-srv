package es.miw.tfm.invierte.user.data.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {

  Optional<Operator> findByEmail(String email);

  List<Operator> findBySystemRoleIn(Collection<SystemRole> roles);

}
