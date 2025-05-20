package es.miw.tfm.invierte.user.configuration;

import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for processing JWT authentication.
 * This filter extracts the JWT token from the `Authorization` header,
 * validates it, and sets the authentication in the security context.
 *
 * <p>Extends `OncePerRequestFilter` to ensure the filter is executed once per request.
 *
 * @see JwtService
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *
 * @author denilssonmn
 * @author dev_castle
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION = "Authorization";

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws IOException, ServletException {
    List<GrantedAuthority> authorities = new ArrayList<>();

    String token = this.jwtService.extractToken(request.getHeader(AUTHORIZATION));
    if (!token.isEmpty()) {
      authorities.add(new SimpleGrantedAuthority(CompanyRole.PREFIX + jwtService.role(token)));
    }

    this.jwtService.roles(token)
        .forEach((companyKey, roleValue) ->
            authorities.add(
                new SimpleGrantedAuthority(CompanyRole.PREFIX + companyKey + "_" + roleValue)));

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(jwtService.user(token), token,authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(request, response);
  }

}
