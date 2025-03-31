package es.miw.tfm.invierte.user.service;

import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.EMAIL;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperatorInfoDto;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperatorInfoChanged;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomOperatorSupport;
import static es.miw.tfm.invierte.user.util.DummyOperatorUtil.createRandomPasswordChangeDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.data.dao.OperatorRepository;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.exception.BadRequestException;
import es.miw.tfm.invierte.user.service.exception.ConflictException;
import es.miw.tfm.invierte.user.service.exception.ForbiddenException;
import es.miw.tfm.invierte.user.service.exception.NotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith({MockitoExtension.class})
class OperatorServiceTest {

  private static final String TOKEN = "token";

  @InjectMocks
  private OperatorService operatorService;

  @Mock
  private OperatorRepository operatorRepository;

  @Mock
  private JwtService jwtService;

  @Captor
  ArgumentCaptor<Operator> operatorCaptor;

  @Spy
  private Operator operator;

  @BeforeEach
  void setUp() {
    operator = createRandomOperatorSupport();
  }

  @Test
  void testChangePasswordWhenIsOk() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    final var passwordChangeDto = createRandomPasswordChangeDto();
    final var operatorChangePass = createRandomOperatorSupport();
    operatorChangePass.setPassword(new BCryptPasswordEncoder()
      .encode(passwordChangeDto.getNewPassword()));
    when(operatorRepository.save(isA(Operator.class))).thenReturn(operatorChangePass);
    assertDoesNotThrow(() -> operatorService.changePassword(EMAIL, passwordChangeDto));
  }

  @Test
  void testChangePasswordWhenOperatorNotFound() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () ->
      operatorService.changePassword(EMAIL, createRandomPasswordChangeDto()));
  }

  @Test
  void testChangePasswordWhenOldPasswordMismatch() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    PasswordChangeDto wrongDto = createRandomPasswordChangeDto();
    wrongDto.setNewPassword("wrong");
    assertThrows(BadRequestException.class, () ->
            operatorService.changePassword(EMAIL, wrongDto)
    );
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

  @Test
  void testCreateUserSuccess() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    operatorService.createUser(operator, SystemRole.ADMIN);
    verify(operatorRepository).save(any(Operator.class));
  }

  @Test
  void testLoginNotFound() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> operatorService.login(EMAIL));
  }

  @Test
  void testLoginSuccess() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    when(jwtService.createToken(anyString(), anyString(), anyString())).thenReturn(TOKEN);

    String actualToken = operatorService.login(EMAIL);

    verify(operatorRepository).findByEmail(EMAIL);
    verify(jwtService).createToken(EMAIL, "Temp", "SUPPORT");
    assertEquals(TOKEN, actualToken);
  }

  @Test
  void testUpdateGeneralInfoWhenIsOk() {
    final var operator = createRandomOperatorSupport();
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    when(operatorRepository.save(operatorCaptor.capture())).thenReturn(operator);
    final var operatorInfoDto = createRandomOperatorInfoChanged();
    operatorService.updateGeneralInfo(EMAIL, operatorInfoDto);
    assertEquals(operatorInfoDto.getFirstName(), operatorCaptor.getValue().getFirstName());
    assertEquals(operatorInfoDto.getFamilyName(), operatorCaptor.getValue().getFamilyName());
  }

  @Test
  void testUpdateGeneralInfoWhenIsNotFound() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    var operatorInfoDto = createRandomOperatorInfoDto();
    assertThrows(NotFoundException.class, () ->
      operatorService.updateGeneralInfo(EMAIL, operatorInfoDto));
  }

  @Test
  void testReadGeneralInfoWhenIsOk() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(operator));
    var infoDto = operatorService.readGeneralInfo(EMAIL);
    assertEquals(operator.getFirstName(), infoDto.getFirstName());
    verify(operatorRepository).findByEmail(EMAIL);
  }

  @Test
  void testReadGeneralInfoWhenIsNotFound() {
    when(operatorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () ->
            operatorService.readGeneralInfo(EMAIL));
  }

}
