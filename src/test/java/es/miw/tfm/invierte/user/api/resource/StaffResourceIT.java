package es.miw.tfm.invierte.user.api.resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Base64;

import es.miw.tfm.invierte.user.ApiTestConfig;
import es.miw.tfm.invierte.user.BaseContainerIntegrationTest;
import es.miw.tfm.invierte.user.api.dto.StaffCompanyDto;
import es.miw.tfm.invierte.user.api.dto.StaffDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiTestConfig
@DirtiesContext
public class StaffResourceIT extends BaseContainerIntegrationTest {

  private static final String PASSWORD = "tempassword";

  private static final String TAX_IDENTIFIER_NUMBER = "12345678A";

  private static final String ACTIVATION_CODE = "999999999999";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private StaffRepository staffRepository;

  @BeforeAll
  public static void setup() {
    postgreSQLContainer.start();
  }

  @AfterAll
  public static void clean() {
    postgreSQLContainer.close();
  }

  public static Staff createRandomInactiveStaff() {
    Staff staff = new Staff();
    staff.setFirstName("Temp");
    staff.setFamilyName("Temp1");
    staff.setEmail("temp@email.com");
    staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
    staff.setCompanyRole(CompanyRole.OWNER);
    staff.setStatus(Status.INACTIVE);
    return staff;
  }

  public static Staff createRandomActiveStaff() {
    Staff staff = new Staff();
    staff.setFirstName("Temp");
    staff.setFamilyName("Temp1");
    staff.setEmail("temp@email.com");
    staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
    staff.setCompanyRole(CompanyRole.OWNER);
    staff.setStatus(Status.INACTIVE);
    return staff;
  }

  public static Staff createRandomInactiveStaffWithCompany() {
    Staff staff = new Staff();
    staff.setFirstName("Temp");
    staff.setFamilyName("Temp1");
    staff.setEmail("temp@email.com");
    staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
    staff.setCompanyRole(CompanyRole.OWNER);
    staff.setTaxIdentifierNumber(TAX_IDENTIFIER_NUMBER);
    staff.setStatus(Status.INACTIVE);

    ActivationCode activationCode = new ActivationCode();
    activationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
    activationCode.setCode(ACTIVATION_CODE);
    activationCode.setStaff(staff);
    staff.getActivationCodes().add(activationCode);

    return staff;
  }

  public static StaffDto createRandomStaffDto() {
    return StaffDto.builder()
        .firstName("new")
        .familyName("new")
        .email("new@email.com")
        .password("newpass")
        .companyRole(CompanyRole.AGENT)
        .build();
  }

  @Test
  void testLoginStaff() {
    final var mockedEntity = createRandomActiveStaff();
    this.staffRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    webTestClient.post().uri(StaffResource.USERS + StaffResource.STAFF + StaffResource.TOKEN)
        .header("Authorization", basicAuth)
        .exchange()
        .expectStatus().isOk()
        .expectBody(TokenDto.class)
        .value(response -> {
          assertNotNull(response.getToken());
        });
    this.staffRepository.deleteAll();
  }

  @Test
  void testCreateUserWithNoCompany() {
    final var newMockedStaffDto = createRandomStaffDto();
    webTestClient.post().uri(StaffResource.USERS + StaffResource.STAFF + StaffResource.NO_COMPANY)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(newMockedStaffDto)
        .exchange()
        .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }

  @Test
  void testSetCompanyToUser() {
    final var mockedEntity = createRandomInactiveStaff();
    this.staffRepository.save(mockedEntity);
    final var staffCompanyDto = new StaffCompanyDto("12345678A");
    webTestClient.patch().uri(StaffResource.USERS + StaffResource.STAFF + "/" + mockedEntity.getEmail() + StaffResource.SET_COMPANY)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(staffCompanyDto)
        .exchange()
        .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }

  @Test
  void testNotify() {
    final var mockedEntity = createRandomInactiveStaff();
    this.staffRepository.save(mockedEntity);
    webTestClient.post().uri(
            StaffResource.USERS + StaffResource.STAFF + "/" + mockedEntity.getEmail() + StaffResource.COMPANY + "/12345678A"
                + StaffResource.NOTIFY_CODE)
        .exchange()
        .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }

  @Test
  void testActivateAccount() {

    final var mockedEntity = createRandomInactiveStaffWithCompany();
    this.staffRepository.save(mockedEntity);

    webTestClient.post()
        .uri(StaffResource.USERS + StaffResource.STAFF + StaffResource.ACTIVATE_CODE.replace("{activationCode}", ACTIVATION_CODE))
        .exchange()
        .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }
}
