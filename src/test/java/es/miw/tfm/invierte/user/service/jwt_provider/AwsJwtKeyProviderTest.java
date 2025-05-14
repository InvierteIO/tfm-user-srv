package es.miw.tfm.invierte.user.service.jwt_provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@ExtendWith(MockitoExtension.class)
class AwsJwtKeyProviderTest {

  private String readPemFile(String path) throws Exception {
    return new String(Files.readAllBytes(Paths.get(Objects
        .requireNonNull(getClass()
            .getClassLoader()
            .getResource(path))
        .toURI())));
  }

  @Test
  void shouldLoadKeysFromAwsSecretsManagerUsingPemFiles() throws Exception {
    String privatePem = readPemFile("key/mock_private.pem");
    String publicPem = readPemFile("key/mock_public.pem");

    SecretsManagerClient mockClient = mock(SecretsManagerClient.class);

    try (MockedStatic<SecretsManagerClient> staticClient = mockStatic(SecretsManagerClient.class)) {
      staticClient.when(SecretsManagerClient::create).thenReturn(mockClient);

      when(mockClient.getSecretValue(any(GetSecretValueRequest.class)))
          .thenReturn(GetSecretValueResponse.builder().secretString(privatePem).build())
          .thenReturn(GetSecretValueResponse.builder().secretString(publicPem).build());

      AwsJwtKeyProvider provider = new AwsJwtKeyProvider("mock-private-arn", "mock-public-arn");

      RSAPrivateKey privateKey = provider.getPrivateKey();
      RSAPublicKey publicKey = provider.getPublicKey();

      assertNotNull(privateKey);
      assertNotNull(publicKey);
      assertEquals("RSA", privateKey.getAlgorithm());
      assertEquals("RSA", publicKey.getAlgorithm());
    }
  }
}
