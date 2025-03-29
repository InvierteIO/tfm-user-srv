package es.miw.tfm.invierte.user.api.resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Base64;

import es.miw.tfm.invierte.user.ApiTestConfig;
import es.miw.tfm.invierte.user.BaseContainerIntegrationTest;
import es.miw.tfm.invierte.user.api.dto.OperatorDto;
import es.miw.tfm.invierte.user.api.dto.TokenDto;
import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
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
public class OperatorResourceIT extends BaseContainerIntegrationTest {

  private static final String PASSWORD = "tempassword";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private OperatorRepository operatorRepository;

  @BeforeAll
  public static void setup() {
    postgreSQLContainer.start();
  }

  @AfterAll
  public static void clean() {
    postgreSQLContainer.close();
  }

  public static Operator createRandomOperator() {
    Operator operator = new Operator();
    operator.setFirstName("Temp");
    operator.setFamilyName("Temp1");
    operator.setEmail("temp@email.com");
    operator.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
    operator.setSystemRole(SystemRole.ADMIN);
    return operator;
  }

  public static OperatorDto createRandomOperatorDto() {
    return OperatorDto.builder()
        .firstName("new")
        .familyName("new")
        .email("new@email.com")
        .password("newpass")
        .systemRole(SystemRole.SUPPORT)
        .build();
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
  void testCreateUserOperator() {
    final var mockedEntity = createRandomOperator();
    final var newMockedOperatorDto = createRandomOperatorDto();
    this.operatorRepository.save(mockedEntity);
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((mockedEntity.getEmail() + ":" + PASSWORD).getBytes());
    String token = webTestClient.post().uri(OperatorResource.USERS + OperatorResource.OPERATOR + OperatorResource.TOKEN)
        .header("Authorization", basicAuth)
        .exchange()
        .expectBody(TokenDto.class)
        .returnResult()
        .getResponseBody()
        .getToken();

    String bearer = "Bearer " + token;

    webTestClient.post().uri(OperatorResource.USERS + OperatorResource.OPERATOR)
        .header("Authorization", bearer)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(newMockedOperatorDto)
        .exchange()
        .expectStatus().isOk();

    this.operatorRepository.deleteAll();
  }

}
