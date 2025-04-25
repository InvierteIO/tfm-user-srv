package es.miw.tfm.invierte.user.configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

@Component
@Slf4j
@Getter
public class KeyConfiguration {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.public-secret-name}")
  private String publicSecretName;

  @Value("${aws.private-secret-name}")
  private String privateSecretName;

  private PrivateKey privateKey;

  private PublicKey publicKey;

  @PostConstruct
  public void init() {
    try (SecretsManagerClient client = SecretsManagerClient.builder()
        .region(Region.of(region))
        .build()){
      this.privateKey = this.getPrivateKeyFromSecretsManager(client, privateSecretName);
      this.publicKey = this.getPublicKeyFromSecretsManager(client, publicSecretName);
    } catch (SecretsManagerException e) {
      log.error("Failed to fetch secret from AWS Secrets Manager", e);
      throw new RuntimeException("Failed to fetch secret from AWS Secrets Manager", e);
    } catch (Exception e) {
      log.error("Failed to initialize RSA private key", e);
      throw new RuntimeException("Failed to initialize RSA private key", e);
    }
  }

  private PrivateKey getPrivateKeyFromSecretsManager(SecretsManagerClient client, String secretName) throws Exception {
    GetSecretValueRequest requestPrivate = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build();
    GetSecretValueResponse response = client.getSecretValue(requestPrivate);
    return this.convertPemToPrivateKey(response.secretString());
  }

  private PublicKey getPublicKeyFromSecretsManager(SecretsManagerClient client, String secretName) throws Exception {
    GetSecretValueRequest requestPublic = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build();
    GetSecretValueResponse response = client.getSecretValue(requestPublic);
    return this.parsePemToPublicKey(response.secretString());
  }

  private PrivateKey convertPemToPrivateKey(String pem) throws Exception {
    String cleanPem = pem
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");

    byte[] keyBytes = Base64.getDecoder().decode(cleanPem);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    return KeyFactory.getInstance("RSA").generatePrivate(spec);
  }

  private PublicKey parsePemToPublicKey(String pem) throws Exception {
    String cleaned = pem
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    byte[] decoded = Base64.getDecoder().decode(cleaned);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePublic(spec);
  }

}
