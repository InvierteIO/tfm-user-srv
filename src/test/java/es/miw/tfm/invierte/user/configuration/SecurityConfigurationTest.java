package es.miw.tfm.invierte.user.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.dao.StaffRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@ExtendWith({MockitoExtension.class})
class SecurityConfigurationTest {

  @Mock
  private HttpSecurity httpSecurity;

  @Mock
  private OperatorRepository operatorRepository;

  @Mock
  private StaffRepository staffRepository;

  @InjectMocks
  private SecurityConfiguration securityConfiguration;

  private static final String EMAIL = "est@email.com";

  @Test
  void testUserDetailsServiceOperator() {
    final var mockedOperatorOpt = mockedOperator();
    when(this.operatorRepository.findByEmail(EMAIL)).thenReturn(mockedOperatorOpt);

    final var actualResponse = this.securityConfiguration.userDetailsService()
        .loadUserByUsername(EMAIL);

    assertNotNull(actualResponse);
    assertFalse(mockedOperatorOpt.isEmpty());
    assertEquals(mockedOperatorOpt.get().getEmail(), actualResponse.getUsername());
    assertEquals(mockedOperatorOpt.get().getPassword(), actualResponse.getPassword());
    assertTrue(actualResponse.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().endsWith(mockedOperatorOpt.get().getSystemRole().name())));
  }

  @Test
  void testUserDetailsServiceStaff() {
    final var mockedStaffOpt = mockedStaff();
    when(this.operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    when(this.staffRepository.findByEmail(EMAIL)).thenReturn(mockedStaffOpt);

    final var actualResponse = this.securityConfiguration.userDetailsService()
        .loadUserByUsername(EMAIL);

    assertNotNull(actualResponse);
    assertFalse(mockedStaffOpt.isEmpty());
    assertEquals(mockedStaffOpt.get().getEmail(), actualResponse.getUsername());
    assertEquals(mockedStaffOpt.get().getPassword(), actualResponse.getPassword());
  }

  @Test
  void testSecurityFilterChainBean() throws Exception {
    SecurityFilterChain mockSecurityFilterChain = mock(SecurityFilterChain.class);

    when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
    when(httpSecurity.httpBasic(any())).thenReturn(httpSecurity);
    when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
    when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
    when(httpSecurity.addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class)))
        .thenReturn(httpSecurity);
    when(securityConfiguration.securityFilterChain(httpSecurity)).thenReturn(mockSecurityFilterChain);

    final var actualResult = securityConfiguration.securityFilterChain(httpSecurity);

    assertNotNull(actualResult);
  }

  @Test
  void testAuthenticationProviderBean() {
    final var  actualAuthenticationProvider = this.securityConfiguration.authenticationProvider();
    assertNotNull(actualAuthenticationProvider);
  }

  @Test
  void testAuthenticationManagerBean() throws Exception {
    AuthenticationConfiguration mockConfig = mock(AuthenticationConfiguration.class);
    AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
    when(mockConfig.getAuthenticationManager()).thenReturn(mockAuthenticationManager);

    AuthenticationManager authenticationManager = this.securityConfiguration.authenticationManager(mockConfig);

    assertNotNull(authenticationManager);
    assertEquals(mockAuthenticationManager, authenticationManager);
    verify(mockConfig).getAuthenticationManager();
  }

  @Test
  void testJwtAuthenticationFilterBean() {
    JwtAuthenticationFilter jwtAuthenticationFilter = this.securityConfiguration.jwtAuthorizationFilter();
    assertNotNull(jwtAuthenticationFilter);
  }

  @Test
  void testPasswordEncoderBean() {
    PasswordEncoder passwordEncoder = this.securityConfiguration.passwordEncoder();
    assertNotNull(passwordEncoder);
  }

  private Optional<Operator> mockedOperator(){
    return Optional.of(Operator.builder()
        .email("test@example.com")
        .password("securePassword")
        .systemRole(SystemRole.ADMIN)
        .id(1)
        .build());
  }

  private Optional<Staff> mockedStaff(){
    return Optional.of(Staff.builder()
        .email(EMAIL)
        .password("securePassword")
        .id(1)
        .build());
  }

}
