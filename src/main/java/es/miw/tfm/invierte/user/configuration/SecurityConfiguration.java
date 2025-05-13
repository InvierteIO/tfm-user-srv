package es.miw.tfm.invierte.user.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configuration class for Spring Security.
 * This class sets up the security configuration for the application, including
 * authentication, password encoding, and JWT-based authorization.
 *
 * <p>Utilizes Spring Security's configuration capabilities to define security filters,
 * authentication providers, and session management policies.
 *
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 * @see org.springframework.security.web.SecurityFilterChain
 * @see org.springframework.security.authentication.AuthenticationManager
 * @see org.springframework.security.crypto.password.PasswordEncoder
 * @see JwtAuthenticationFilter
 * @see JwtService
 * @see OperatorRepository
 * @see StaffRepository
 *
 * @author denilssonmn
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final OperatorRepository operatorRepository;

  private final StaffRepository staffRepository;

  private final JwtService jwtService;

  /**
   * Provides a custom `UserDetailsService` implementation.
   * Retrieves user details from either the `OperatorRepository` or `StaffRepository`
   * based on the provided email.
   *
   * @return a `UserDetailsService` implementation
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return email -> {
      final var operator = operatorRepository.findByEmail(email);
      if (operator.isPresent()) {
        final var operatorFound = operator.get();
        return org.springframework.security.core.userdetails.User.builder()
            .username(operatorFound.getEmail())
            .password(operatorFound.getPassword())
            .roles(operatorFound.getSystemRole().name())
            .build();
      }

      final var staff = staffRepository.findByEmail(email)
          .stream()
          .findFirst();

      if (staff.isPresent()) {
        final var staffFound = staff.get();
        return org.springframework.security.core.userdetails.User.builder()
            .username(staffFound.getEmail())
            .password(staffFound.getPassword())
            .build();
      }

      throw new BadCredentialsException("Bad credentials");
    };
  }

  /**
   * Configures the security filter chain for the application.
   * Disables CSRF, enables HTTP Basic authentication, sets session management to stateless,
   * and adds a JWT authorization filter.
   *
   * @param http the `HttpSecurity` object to configure
   * @return the configured `SecurityFilterChain`
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    RequestMatcher csrfExcludedEndpoints = new OrRequestMatcher(
        new AntPathRequestMatcher("/users/**")
    );

    http
        .csrf(csrf -> csrf.ignoringRequestMatchers(csrfExcludedEndpoints))
        .httpBasic(withDefaults())
        .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthorizationFilter(),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Configures the authentication provider for the application.
   * Uses a `DaoAuthenticationProvider` with a custom `UserDetailsService` and
   * a password encoder.
   *
   * @return the configured `AuthenticationProvider`
   */
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Provides the `AuthenticationManager` bean.
   * Retrieves the `AuthenticationManager` from the `AuthenticationConfiguration`.
   *
   * @param config the `AuthenticationConfiguration` object
   * @return the `AuthenticationManager`
   * @throws Exception if an error occurs during retrieval
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Provides the JWT authorization filter bean.
   * Configures the `JwtAuthenticationFilter` with the `JwtService`.
   *
   * @return the `JwtAuthenticationFilter`
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthorizationFilter() {
    return new JwtAuthenticationFilter(jwtService);
  }

  /**
   * Provides the password encoder bean.
   * Uses the `BCryptPasswordEncoder` for encoding passwords.
   *
   * @return the `PasswordEncoder`
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
