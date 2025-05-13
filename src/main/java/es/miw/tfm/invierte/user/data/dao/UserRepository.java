package es.miw.tfm.invierte.user.data.dao;

import es.miw.tfm.invierte.user.data.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing `User` entities.
 * This interface provides methods for performing CRUD operations and custom queries
 * on the `User` entity. It extends the `JpaRepository` interface.
 *
 * @see es.miw.tfm.invierte.user.data.model.User
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @see java.util.Optional
 *
 * @author denilssonmn
 */
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
}
