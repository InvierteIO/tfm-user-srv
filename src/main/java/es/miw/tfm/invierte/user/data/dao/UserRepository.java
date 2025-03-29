package es.miw.tfm.invierte.user.data.dao;

import java.util.Optional;

import es.miw.tfm.invierte.user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
}
