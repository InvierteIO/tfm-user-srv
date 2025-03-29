package es.miw.tfm.invierte.user.data.dao;

import java.util.Optional;

import es.miw.tfm.invierte.user.data.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {

  Optional<Operator> findByEmail(String email);

}
