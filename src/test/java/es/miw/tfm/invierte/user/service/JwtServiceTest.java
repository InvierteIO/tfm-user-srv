package es.miw.tfm.invierte.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import es.miw.tfm.invierte.user.service.jwt_provider.LocalJwtKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private JwtService jwtService;

  private static final String ISSUER = "testIssuer";
  private static final int EXPIRE = 3600;

  @BeforeEach
  void setUp() throws NoSuchAlgorithmException {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    LocalJwtKeyProvider jwtKeyProvider = mock(LocalJwtKeyProvider.class);
    when(jwtKeyProvider.getPublicKey()).thenReturn((RSAPublicKey) keyPair.getPublic());
    when(jwtKeyProvider.getPrivateKey()).thenReturn((RSAPrivateKey) keyPair.getPrivate());

    this.jwtService = new JwtService(jwtKeyProvider, ISSUER, EXPIRE);
  }

  @Test
  void testExtractToken() {
    String bearerToken = "Bearer abc.def.ghi";
    String extractedToken = this.jwtService.extractToken(bearerToken);
    assertEquals("abc.def.ghi", extractedToken);
  }

  @Test
  void testExtractTokenInvalid() {
    String invalidBearer = "InvalidToken";
    String extractedToken = this.jwtService.extractToken(invalidBearer);
    assertEquals("", extractedToken);
  }

  @Test
  void testCreateTokenWithRole() {
    String token = this.jwtService.createToken("user1", "User One", "ROLE_USER");
    assertEquals(3, token.split("\\.").length); // Valid JWT format
  }

  @Test
  void testCreateTokenWithCompanyRoles() {
    Map<String, String> companyRoles = Map.of("CompanyA", "ROLE_ADMIN", "CompanyB", "ROLE_USER");
    String token = this.jwtService.createToken("user2", "User Two", companyRoles);
    assertEquals(3, token.split("\\.").length); // Valid JWT format
  }

  @Test
  void testUser() {
    String token = this.jwtService.createToken("user1", "User One", "ROLE_USER");
    String user = this.jwtService.user(token);
    assertEquals("user1", user);
  }

  @Test
  void testName() {
    String token = this.jwtService.createToken("user1", "User One", "ROLE_USER");
    String name = this.jwtService.name(token);
    assertEquals("User One", name);
  }

  @Test
  void testRole() {
    String token = this.jwtService.createToken("user1", "User One", "ROLE_USER");
    String role = this.jwtService.role(token);
    assertEquals("ROLE_USER", role);
  }

}
