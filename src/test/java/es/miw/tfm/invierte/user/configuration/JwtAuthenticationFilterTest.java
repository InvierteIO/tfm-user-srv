package es.miw.tfm.invierte.user.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import es.miw.tfm.invierte.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith({MockitoExtension.class})
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void testDoFilterInternal_ValidToken() throws ServletException, IOException {
    String token = "validToken";
    String username = "test@example.com";
    String role = "ROLE_USER";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    when(this.jwtService.extractToken("Bearer " + token)).thenReturn(token);
    when(this.jwtService.user(token)).thenReturn(username);
    when(this.jwtService.role(token)).thenReturn(role);

    this.jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    assertEquals(token, SecurityContextHolder.getContext().getAuthentication().getCredentials());
    assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role)));

    verify(filterChain).doFilter(request, response);
  }

}
