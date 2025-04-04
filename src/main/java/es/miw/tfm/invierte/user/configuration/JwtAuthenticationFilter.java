package es.miw.tfm.invierte.user.configuration;

import java.io.IOException;
import java.util.List;

import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION = "Authorization";

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws IOException, ServletException {
    String token = this.jwtService.extractToken(request.getHeader(AUTHORIZATION));
    if (!token.isEmpty()) {
      GrantedAuthority authority = new SimpleGrantedAuthority(CompanyRole.PREFIX + jwtService.role(token));
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(jwtService.user(token), token, List.of(authority));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(request, response);
  }

}
