package es.miw.tfm.invierte.user.api.resource;

import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.PASSWORD;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperator;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperatorDto;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperatorInfoDto;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomPasswordChangeDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Base64;

import es.miw.tfm.invierte.user.ApiTestConfig;
import es.miw.tfm.invierte.user.BaseContainerIntegration;
import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiTestConfig
@DirtiesContext
@Tag("IntegrationTest")
class OperatorResourceIT extends BaseContainerIntegration {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private OperatorRepository operatorRepository;

  @BeforeAll
  static void setup() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void clean() {
    postgreSQLContainer.close();
  }

  @Test
  void testChangePasswordOperator() {
    final var mockedEntity = createRandomOperator();
    final var changePasswordDto = createRandomPasswordChangeDto();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.patch().uri(OperatorResource.USERS + OperatorResource.OPERATOR +
                    "/" + mockedEntity.getEmail()+ OperatorResource.CHANGE_PASSWORD)
      .header("Authorization", bearer)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(changePasswordDto)
      .exchange()
      .expectStatus().isOk();

    this.operatorRepository.deleteAll();
  }

  @Test
  void testCreateUserOperator() {
    final var newMockedOperatorDto = createRandomOperatorDto();
    final var mockedEntity = createRandomOperator();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.post().uri(OperatorResource.USERS + OperatorResource.OPERATOR)
            .header("Authorization", bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newMockedOperatorDto)
            .exchange()
            .expectStatus().isOk();

    this.operatorRepository.deleteAll();
  }

  @Test
  void testGetOperatorInfoDto() {
    final var mockedEntity = createRandomOperator();
    final var operatorInfoDto = createRandomOperatorInfoDto();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.get().uri(OperatorResource.USERS + OperatorResource.OPERATOR +
                    "/" + mockedEntity.getEmail()+ OperatorResource.GENERAL_INFO)
      .header("Authorization", bearer)
      .exchange()
      .expectStatus().isOk()
      .expectBody(OperatorInfoDto.class)
      .value(operatorInfoDtoResponse -> {
         assertEquals(operatorInfoDto.getFirstName(),operatorInfoDtoResponse.getFirstName());
         assertEquals(operatorInfoDto.getFamilyName(),operatorInfoDtoResponse.getFamilyName());
      });
    this.operatorRepository.deleteAll();
  }

  @Test
  void testLoginOperator() {
    final var mockedEntity = createRandomOperator();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    webTestClient.post().uri(OperatorResource.USERS + OperatorResource.OPERATOR + OperatorResource.TOKEN)
      .header("Authorization", basicAuth)
      .exchange()
      .expectStatus().isOk()
      .expectBody(TokenDto.class)
      .value(response -> {
        assertNotNull(response.getToken());
      });
    this.operatorRepository.deleteAll();
  }

  @Test
  void testUpdateGeneralInfo() {
    final var mockedEntity = createRandomOperator();
    final var operatorInfoDto = createRandomOperatorInfoDto();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String bearer = generateBearerToken(basicAuth);
    webTestClient.patch().uri(OperatorResource.USERS + OperatorResource.OPERATOR +
                    "/" + mockedEntity.getEmail()+ OperatorResource.GENERAL_INFO)
      .header("Authorization", bearer)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(operatorInfoDto)
      .exchange()
      .expectStatus().isOk();
    this.operatorRepository.deleteAll();
  }


  private String generateBearerToken(String basicAuth) {
    String token = webTestClient.post().uri(OperatorResource.USERS + OperatorResource.OPERATOR + OperatorResource.TOKEN)
      .header("Authorization", basicAuth)
      .exchange()
      .expectBody(TokenDto.class)
      .returnResult()
      .getResponseBody()
      .getToken();

    return "Bearer " + token;
  }

}
