package es.miw.tfm.invierte.user.data.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GenderTest {

  @Test
  void testGenderValues() {
    Gender[] genders = Gender.values();
    assertNotNull(genders);
    assertEquals(2, genders.length);
    assertEquals(Gender.MALE, genders[0]);
    assertEquals(Gender.FEMALE, genders[1]);
  }

  @Test
  void testGenderValueOf() {
    assertEquals(Gender.MALE, Gender.valueOf("MALE"));
    assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
  }

}
