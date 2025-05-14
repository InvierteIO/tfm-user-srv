package es.miw.tfm.invierte.user.service.jwt_provider;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

/**
 * An implementation of the {@link JwtKeyProvider} interface for production environments.
 * This class retrieves RSA private and public keys from AWS Secrets Manager.
 * It is active only in the "prod" profile.
 *
 * @author denilssonmn
 */
@Component
@Profile("prod")
public class AwsJwtKeyProvider implements JwtKeyProvider {

  private final RSAPrivateKey privateKey;

  private final RSAPublicKey publicKey;

  /**
   * Constructs an {@code AwsJwtKeyProvider} and retrieves the RSA private and public keys
   * from AWS Secrets Manager using the specified secret IDs.
   *
   * @param privateKeySecret the secret ID for the RSA private key in AWS Secrets Manager
   * @param publicKeySecret the secret ID for the RSA public key in AWS Secrets Manager
   * @throws Exception if an error occurs while retrieving or parsing the keys
   */
  public AwsJwtKeyProvider(
      @Value("${tfm.jwt.private-key-secret}") String privateKeySecret,
      @Value("${tfm.jwt.public-key-secret}") String publicKeySecret
  ) throws Exception {
    SecretsManagerClient client = SecretsManagerClient.create();
    this.privateKey = loadPrivateKey(getSecret(client, privateKeySecret));
    this.publicKey = loadPublicKey(getSecret(client, publicKeySecret));
  }

  private String getSecret(SecretsManagerClient client, String secretId) {
    return client
        .getSecretValue(GetSecretValueRequest.builder()
            .secretId(secretId)
            .build())
        .secretString();
  }

  private RSAPrivateKey loadPrivateKey(String pem) throws Exception {
    String key = pem.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
  }

  private RSAPublicKey loadPublicKey(String pem) throws Exception {
    String key = pem.replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(key);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
  }

  @Override
  public RSAPrivateKey getPrivateKey() {
    return privateKey;
  }

  @Override
  public RSAPublicKey getPublicKey() {
    return publicKey;
  }
}
