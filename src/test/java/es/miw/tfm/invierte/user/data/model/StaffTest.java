package es.miw.tfm.invierte.user.data.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import org.junit.jupiter.api.Test;

class StaffTest {

  private final Staff staff = new Staff();

  @Test
  void testSetDefaultNoCompany() {
    this.staff.setDefaultNoCompany();
    assertEquals(Status.INACTIVE, staff.getStatus());
    assertEquals(CompanyRole.OWNER, staff.getCompanyRole());
    assertNull(staff.getTaxIdentificationNumber());
  }


  @Test
  void testSetNewCompanyUserDefault() {
    Staff staff = new Staff();
    staff.setPassword("securePassword123");

    staff.setNewCompanyUserDefault();

    assertEquals(Status.INACTIVE, staff.getStatus());
    assertNull(staff.getPassword());
  }
}
