package es.miw.tfm.invierte.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private JwtService jwtService;

  private static final String SECRET = "testSecret";
  private static final String ISSUER = "testIssuer";
  private static final int EXPIRE = 3600;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService(SECRET, ISSUER, EXPIRE);
  }

  @Test
  void testExtractToken() {
    String bearerToken = "Bearer abc.def.ghi";
    String extractedToken = jwtService.extractToken(bearerToken);
    assertEquals("abc.def.ghi", extractedToken);
  }

  @Test
  void testExtractTokenInvalid() {
    String invalidBearer = "InvalidToken";
    String extractedToken = jwtService.extractToken(invalidBearer);
    assertEquals("", extractedToken);
  }

  @Test
  void testCreateTokenWithRole() {
    String token = jwtService.createToken("user1", "User One", "ROLE_USER");
    assertEquals(3, token.split("\\.").length); // Valid JWT format
  }

  @Test
  void testCreateTokenWithCompanyRoles() {
    Map<String, String> companyRoles = Map.of("CompanyA", "ROLE_ADMIN", "CompanyB", "ROLE_USER");
    String token = jwtService.createToken("user2", "User Two", companyRoles);
    assertEquals(3, token.split("\\.").length); // Valid JWT format
  }

  @Test
  void testUser() {
    String token = jwtService.createToken("user1", "User One", "ROLE_USER");
    String user = jwtService.user(token);
    assertEquals("user1", user);
  }

  @Test
  void testName() {
    String token = jwtService.createToken("user1", "User One", "ROLE_USER");
    String name = jwtService.name(token);
    assertEquals("User One", name);
  }

  @Test
  void testRole() {
    String token = jwtService.createToken("user1", "User One", "ROLE_USER");
    String role = jwtService.role(token);
    assertEquals("ROLE_USER", role);
  }

}
