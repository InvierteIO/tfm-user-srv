package es.miw.tfm.invierte.user;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class BaseContainerIntegration {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

  @Container
  static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/latest"))
      .withServices(LocalStackContainer.Service.SECRETSMANAGER);

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("aws.region", localstack::getRegion);
    registry.add("aws.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.SECRETSMANAGER).toString());
  }

}
