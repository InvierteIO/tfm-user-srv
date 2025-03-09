package es.miw.tfm.invierte.user.resource;

import es.miw.tfm.invierte.user.BaseContainerIntegrationTest;
import es.miw.tfm.invierte.user.api.dto.UserDto;
import es.miw.tfm.invierte.user.api.resource.UserResource;
import es.miw.tfm.invierte.user.data.dao.UserRepository;
import es.miw.tfm.invierte.user.data.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;


@ApiTestConfig
class UserResourceIT extends BaseContainerIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private UserRepository userRepository;

  @BeforeAll
  public static void setup() {
    postgreSQLContainer.start();
  }

  @Test
  void testReadAll() {
    this.createUser();
    this.webTestClient.get().uri(UserResource.USERS)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UserDto.class)
        .hasSize(1);
  }

  @AfterAll
  public static void cleanup() {
    postgreSQLContainer.stop();
  }

  private void createUser() {
    this.userRepository.save(User
        .builder()
        .email("email@email.com")
        .firstName("firstName")
        .familyName("familyName")
        .build());
  }
}
