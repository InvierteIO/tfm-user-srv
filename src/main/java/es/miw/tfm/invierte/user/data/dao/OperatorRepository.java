package es.miw.tfm.invierte.user.data.dao;

import es.miw.tfm.invierte.user.data.model.Operator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing `Operator` entities.
 * This interface provides methods for performing CRUD operations and custom queries
 * on the `Operator` entity. It extends the `JpaRepository` interface.
 *
 * @see es.miw.tfm.invierte.user.data.model.Operator
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @author denilssonmn
 */
public interface OperatorRepository extends JpaRepository<Operator, Integer> {

  Optional<Operator> findByEmail(String email);

}
