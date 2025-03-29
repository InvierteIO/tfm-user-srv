package es.miw.tfm.invierte.user.data.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CompanyRoleTest {

  @Test
  public void testOf() {
    CompanyRole companyRole = CompanyRole.of("ROLE_OWNER");
    assertEquals(CompanyRole.OWNER, companyRole);
  }

}
