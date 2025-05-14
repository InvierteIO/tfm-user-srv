package es.miw.tfm.invierte.user.service.jwt_provider;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * A local implementation of the {@link JwtKeyProvider} interface for development purposes.
 * This class loads RSA private and public keys from file paths specified in the
 * application properties.
 * It is active only in the "dev" profile.
 *
 * @author denilssonmn
 */
public interface JwtKeyProvider {

  RSAPrivateKey getPrivateKey();

  RSAPublicKey getPublicKey();

}
