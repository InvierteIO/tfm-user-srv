package es.miw.tfm.invierte.user.data.dao;

import java.util.Optional;

import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

  Optional<Staff> findByEmailAndTaxIdentificationNumber(String email, String taxIdentificationNumber);

  Optional<Staff> findByEmailAndStatus(String email, Status status);

  Optional<Staff> findByEmail(String email);

}
