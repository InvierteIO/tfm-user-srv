package es.miw.tfm.invierte.user.data.dao;

import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing `Staff` entities.
 * This interface provides methods for performing CRUD operations and custom queries
 * on the `Staff` entity. It extends the `JpaRepository` interface.
 *
 * @see es.miw.tfm.invierte.user.data.model.Staff
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see es.miw.tfm.invierte.user.data.model.enums.Status
 *
 * @author denilssonmn
 */
public interface StaffRepository extends JpaRepository<Staff, Integer> {

  Optional<Staff> findByEmailAndTaxIdentificationNumber(String email,
      String taxIdentificationNumber);

  Optional<Staff> findByEmailAndStatus(String email, Status status);

  Optional<Staff> findByEmail(String email);

}
