package es.miw.tfm.invierte.user.service.jwt_provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalJwtKeyProviderTest {

  private LocalJwtKeyProvider localJwtKeyProvider;

  @BeforeEach
  void setUp() throws Exception {
    String privateKeyFilePath = "src/main/resources/key/mock_private.pem";
    String publicKeyFilePath = "src/main/resources/key/mock_public.pem";
    localJwtKeyProvider = new LocalJwtKeyProvider(privateKeyFilePath, publicKeyFilePath);
  }

  @Test
  void testGetPrivateKey() {
    RSAPrivateKey privateKey = localJwtKeyProvider.getPrivateKey();
    assertNotNull(privateKey, "Private key should not be null");
  }

  @Test
  void testGetPublicKey() {
    RSAPublicKey publicKey = localJwtKeyProvider.getPublicKey();
    assertNotNull(publicKey, "Public key should not be null");
  }

}
