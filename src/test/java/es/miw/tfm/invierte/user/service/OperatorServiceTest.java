package es.miw.tfm.invierte.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class OperatorServiceTest {

  private static final String TOKEN = "token";

  private static final String EMAIL = "email@email.com";

  @InjectMocks
  private OperatorService operatorService;

  @Mock
  private OperatorRepository operatorRepository;

  @Mock
  private JwtService jwtService;

  private Operator operator;

  @BeforeEach
  void setUp() {
    operator = new Operator();
    operator.setEmail(EMAIL);
    operator.setFirstName("Test");
    operator.setSystemRole(SystemRole.SUPPORT);
  }

  @Test
  void testLoginSuccess() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    when(jwtService.createToken(anyString(), anyString(), anyString())).thenReturn(TOKEN);

    String actualToken = operatorService.login(EMAIL);

    verify(operatorRepository).findByEmail(EMAIL);
    verify(jwtService).createToken(EMAIL, "Test", "SUPPORT");
    assertEquals(TOKEN, actualToken);
  }

  @Test
  void testLoginNotFound() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> operatorService.login(EMAIL));
  }

  @Test
  void testCreateUserSuccess() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    operatorService.createUser(operator, SystemRole.ADMIN);
    verify(operatorRepository).save(any(Operator.class));
  }

  @Test
  void testCreateUserConflict() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    assertThrows(ConflictException.class, () -> operatorService.createUser(operator, SystemRole.ADMIN));
  }

  @Test
  void testCreateUserForbidden() {
    operator.setSystemRole(SystemRole.ADMIN);
    assertThrows(ForbiddenException.class, () -> operatorService.createUser(operator, SystemRole.SUPPORT));
  }

}
