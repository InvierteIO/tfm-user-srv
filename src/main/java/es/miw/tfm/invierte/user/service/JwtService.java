package es.miw.tfm.invierte.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import es.miw.tfm.invierte.user.configuration.KeyConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

  private static final String BEARER = "Bearer ";

  private static final int PARTIES = 3;

  private static final String USER_CLAIM = "user";

  private static final String NAME_CLAIM = "name";

  private static final String COMPANY_ROLE_CLAIM = "companyRoles";

  private static final String ROLE_CLAIM = "role";

  private final String issuer;

  private final int expire;

  private final KeyConfiguration keyConfiguration;


  @Autowired
  public JwtService(@Value("${tfm.jwt.issuer}") String issuer,
      @Value("${tfm.jwt.expire}") int expire, KeyConfiguration keyConfiguration) {
    this.issuer = issuer;
    this.expire = expire;
    this.keyConfiguration = keyConfiguration;
  }

  public String extractToken(String bearer) {
    if (bearer != null && bearer.startsWith(BEARER) && PARTIES == bearer.split("\\.").length) {
      return bearer.substring(BEARER.length());
    } else {
      return "";
    }
  }

  public String createToken(String user, String name, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(USER_CLAIM, user);
    claims.put(NAME_CLAIM, name);
    claims.put(ROLE_CLAIM, role);

    return this.buildToken(claims);
  }

  public String createToken(String user, String name, Map<String, String>  companyRoles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(USER_CLAIM, user);
    claims.put(NAME_CLAIM, name);
    claims.put(COMPANY_ROLE_CLAIM, companyRoles);

    return this.buildToken(claims);
  }

  private String buildToken(Map<String, Object> claims) {
    return Jwts.builder()
        .setIssuer(this.issuer)
        .setIssuedAt(new Date())
        .setNotBefore(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + this.expire * 1000L))
        .setClaims(claims)
        .signWith(keyConfiguration.getPrivateKey(), SignatureAlgorithm.RS256)
        .compact();
  }

  public String user(String authorization) {
    return this.verify(authorization)
        .map(claims -> (String)claims.get(USER_CLAIM))
        .orElse("");
  }

  public String name(String authorization) {
    return this.verify(authorization)
        .map(claims -> (String)claims.get(NAME_CLAIM))
        .orElse("");
  }

  public String role(String authorization) {
    return this.verify(authorization)
        .map(claims -> (String)claims.get(ROLE_CLAIM))
        .orElse("");
  }

  public Optional<Claims> verify(String token) {
    try {
      final var claims = Jwts.parserBuilder()
          .setSigningKey(keyConfiguration.getPublicKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      return Optional.of(claims);
    } catch (JwtException e) {
      log.error("Failed to verify JWT token",e);
    }
    return Optional.empty();
  }

}
