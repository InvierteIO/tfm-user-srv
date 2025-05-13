package es.miw.tfm.invierte.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for handling JSON Web Tokens (JWT).
 * This class provides functionality for creating, verifying, and extracting information
 * from JWT tokens. It uses the `auth0` library for token generation and validation.
 *
 * @see com.auth0.jwt.JWT
 * @see com.auth0.jwt.algorithms.Algorithm
 * @see com.auth0.jwt.interfaces.DecodedJWT
 *
 * @author denilssonmn
 */
@Service
public class JwtService {

  private static final String BEARER = "Bearer ";

  private static final int PARTIES = 3;

  private static final String USER_CLAIM = "user";

  private static final String NAME_CLAIM = "name";

  private static final String COMPANY_ROLE_CLAIM = "companyRoles";

  private static final String ROLE_CLAIM = "role";

  private final String secret;

  private final String issuer;

  private final int expire;

  /**
   * Constructs a `JwtService` with the specified secret, issuer, and expiration time.
   *
   * @param secret the secret key used for signing the JWT
   * @param issuer the issuer of the JWT
   * @param expire the expiration time of the JWT in seconds
   */
  @Autowired
  public JwtService(@Value("${tfm.jwt.secret}") String secret,
      @Value("${tfm.jwt.issuer}") String issuer,
      @Value("${tfm.jwt.expire}") int expire) {
    this.secret = secret;
    this.issuer = issuer;
    this.expire = expire;
  }

  /**
   * Extracts the token from a Bearer authorization header.
   *
   * @param bearer the Bearer authorization header
   * @return the extracted token, or an empty string if invalid
   */
  public String extractToken(String bearer) {
    if (bearer != null && bearer.startsWith(BEARER) && PARTIES == bearer.split("\\.").length) {
      return bearer.substring(BEARER.length());
    } else {
      return "";
    }
  }

  /**
   * Creates a JWT token with the specified user, name, and role.
   *
   * @param user the user identifier
   * @param name the name of the user
   * @param role the role of the user
   * @return the generated JWT token
   */
  public String createToken(String user, String name, String role) {
    return JWT.create()
        .withIssuer(this.issuer)
        .withIssuedAt(new Date())
        .withNotBefore(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + this.expire * 1000L))
        .withClaim(USER_CLAIM, user)
        .withClaim(NAME_CLAIM, name)
        .withClaim(ROLE_CLAIM, role)
        .sign(Algorithm.HMAC256(this.secret));

  }

  /**
   * Creates a JWT token with the specified user, name, and company roles.
   *
   * @param user the user identifier
   * @param name the name of the user
   * @param companyRoles a map of company roles
   * @return the generated JWT token
   */
  public String createToken(String user, String name, Map<String, String> companyRoles) {
    return JWT.create()
        .withIssuer(this.issuer)
        .withIssuedAt(new Date())
        .withNotBefore(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + this.expire * 1000L))
        .withClaim(USER_CLAIM, user)
        .withClaim(NAME_CLAIM, name)
        .withClaim(COMPANY_ROLE_CLAIM, companyRoles)
        .sign(Algorithm.HMAC256(this.secret));

  }

  /**
   * Extracts the user identifier from the authorization token.
   *
   * @param authorization the authorization token
   * @return the user identifier, or an empty string if invalid
   */
  public String user(String authorization) {
    return this.verify(authorization)
        .map(jwt -> jwt.getClaim(USER_CLAIM).asString())
        .orElse("");
  }

  /**
   * Extracts the name from the authorization token.
   *
   * @param authorization the authorization token
   * @return the name, or an empty string if invalid
   */
  public String name(String authorization) {
    return this.verify(authorization)
        .map(jwt -> jwt.getClaim(NAME_CLAIM).asString())
        .orElse("");
  }

  /**
   * Extracts the role from the authorization token.
   *
   * @param authorization the authorization token
   * @return the role, or an empty string if invalid
   */
  public String role(String authorization) {
    return this.verify(authorization)
        .map(jwt -> jwt.getClaim(ROLE_CLAIM).asString())
        .orElse("");
  }

  /**
   * Verifies the validity of the token and decodes it.
   *
   * @param token the token to verify
   * @return an `Optional` containing the decoded JWT if valid, or empty if invalid
   */
  private Optional<DecodedJWT> verify(String token) {
    try {
      return Optional.of(JWT.require(Algorithm.HMAC256(this.secret))
          .withIssuer(this.issuer).build()
          .verify(token));
    } catch (Exception exception) {
      return Optional.empty();
    }
  }

}
