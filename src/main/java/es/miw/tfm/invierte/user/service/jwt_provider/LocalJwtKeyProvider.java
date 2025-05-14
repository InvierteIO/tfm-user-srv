package es.miw.tfm.invierte.user.service.jwt_provider;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A local implementation of the {@link JwtKeyProvider} interface for development purposes.
 * This class loads RSA private and public keys from file paths specified in the
 * application properties.
 * It is active only in the "dev" profile.
 *
 * @author denilssonmn
 */
@Component
@Profile("dev")
public class LocalJwtKeyProvider implements JwtKeyProvider {

  private final RSAPrivateKey privateKey;

  private final RSAPublicKey publicKey;

  public LocalJwtKeyProvider(
      @Value("${tfm.jwt.private-key-path}") String privateKeyPath,
      @Value("${tfm.jwt.public-key-path}") String publicKeyPath) throws Exception {
    this.privateKey = loadPrivateKey(privateKeyPath);
    this.publicKey = loadPublicKey(publicKeyPath);
  }

  private RSAPrivateKey loadPrivateKey(String path) throws Exception {
    String pem = Files.readString(new File(path).toPath());
    String key = pem.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
  }

  private RSAPublicKey loadPublicKey(String path) throws Exception {
    String pem = Files.readString(new File(path).toPath());
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
