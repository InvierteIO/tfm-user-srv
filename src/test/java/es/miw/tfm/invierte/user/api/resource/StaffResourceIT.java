package es.miw.tfm.invierte.user.api.resource;

import static es.miw.tfm.invierte.user.util.DummyStaffUtil.ACTIVATION_CODE;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.PASSWORD;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomActiveStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomInactiveStaffWithCompany;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomPasswordChangeDto;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaff;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaffDto;
import static es.miw.tfm.invierte.user.util.DummyStaffUtil.createRandomStaffInfoDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import es.miw.tfm.invierte.user.ApiTestConfig;
import es.miw.tfm.invierte.user.BaseContainerIntegration;
import es.miw.tfm.invierte.user.api.dto.StaffCompanyDto;
import es.miw.tfm.invierte.user.api.dto.StaffInfoDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import java.util.Base64;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiTestConfig
@DirtiesContext
class StaffResourceIT extends BaseContainerIntegration {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private StaffRepository staffRepository;

  @BeforeAll
  static void setup() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void clean() {
    postgreSQLContainer.close();
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

  @Test
  void testChangePasswordOperator() {
    final var mockedEntity = createRandomStaff(Status.ACTIVE);
    final var changePasswordDto = createRandomPasswordChangeDto();
    this.staffRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.patch().uri(StaffResource.USERS + StaffResource.STAFF +
                    "/" + mockedEntity.getEmail()+ StaffResource.CHANGE_PASSWORD)
      .header("Authorization", bearer)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(changePasswordDto)
      .exchange()
      .expectStatus().isOk();

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
  void testGetStaffInfoDto() {
    final var mockedEntity = createRandomStaff(Status.ACTIVE);
    final var staffInfoDto = createRandomStaffInfoDto();
    this.staffRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.get().uri(StaffResource.USERS + StaffResource.STAFF +
                    "/" + mockedEntity.getEmail()+ StaffResource.GENERAL_INFO)
      .header("Authorization", bearer)
      .exchange()
      .expectStatus().isOk()
      .expectBody(StaffInfoDto.class)
      .value(staffInfoDtoResponse -> {
        assertEquals(staffInfoDto.getFirstName(), staffInfoDtoResponse.getFirstName());
        assertEquals(staffInfoDto.getFamilyName(), staffInfoDtoResponse.getFamilyName());
      });

    this.staffRepository.deleteAll();
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
  void testNotify() {
    final var mockedEntity = createRandomStaff(Status.INACTIVE);
    this.staffRepository.save(mockedEntity);
    webTestClient.post().uri(
                    StaffResource.USERS + StaffResource.STAFF + "/" + mockedEntity.getEmail() + StaffResource.COMPANY + "/12345678A"
                            + StaffResource.NOTIFY_CODE)
            .exchange()
            .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }

  @Test
  void testUpdateGeneralInfo() {
    final var mockedEntity = createRandomStaff(Status.ACTIVE);
    final var staffInfoDto = createRandomStaffInfoDto();
    this.staffRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);

    webTestClient.patch().uri(StaffResource.USERS + StaffResource.STAFF +
                    "/" + mockedEntity.getEmail()+ StaffResource.GENERAL_INFO)
      .header("Authorization", bearer)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(staffInfoDto)
      .exchange()
      .expectStatus().isOk();

    this.staffRepository.deleteAll();
  }

  @Test
  void testSetCompanyToUser() {
    final var mockedEntity = createRandomStaff(Status.INACTIVE);
    this.staffRepository.save(mockedEntity);
    final var staffCompanyDto = new StaffCompanyDto("12345678A");
    webTestClient.patch().uri(StaffResource.USERS + StaffResource.STAFF + "/" + mockedEntity.getEmail() + StaffResource.SET_COMPANY)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(staffCompanyDto)
      .exchange()
      .expectStatus().isOk();
    this.staffRepository.deleteAll();
  }

  private String generateBearerToken(String basicAuth) {
    return webTestClient.post().uri(StaffResource.USERS + StaffResource.STAFF + StaffResource.TOKEN)
      .header("Authorization", basicAuth)
      .exchange()
      .expectBody(TokenDto.class)
      .returnResult()
      .getResponseBody()
      .getToken();
  }

}
