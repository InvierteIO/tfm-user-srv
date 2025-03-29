package es.miw.tfm.invierte.user.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final OperatorRepository operatorRepository;

  private final StaffRepository staffRepository;

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

      throw new UsernameNotFoundException("User not found");
    };
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(withDefaults())
        .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthorizationFilter(),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthorizationFilter() {
    return new JwtAuthenticationFilter();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
