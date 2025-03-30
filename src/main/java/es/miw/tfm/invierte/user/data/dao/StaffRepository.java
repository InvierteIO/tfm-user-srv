package es.miw.tfm.invierte.user.data.dao;

import java.util.List;

import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

  List<Staff> findByEmailAndTaxIdentificationNumber(String email, String taxIdentificationNumber);

  List<Staff> findByEmailAndStatus(String email, Status status);

  List<Staff> findByEmail(String email);

}
