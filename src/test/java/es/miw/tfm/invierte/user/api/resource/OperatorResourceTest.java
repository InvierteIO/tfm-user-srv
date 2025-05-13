package es.miw.tfm.invierte.user.api.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.miw.tfm.invierte.user.api.dto.OperatorDto;
import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import es.miw.tfm.invierte.user.service.OperatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import testutils.auth.OperatorAuthentication;

@ExtendWith({MockitoExtension.class})
public class OperatorResourceTest {

  @InjectMocks
  private OperatorResource operatorResource;

  @Mock
  private OperatorService operatorService;

  public static final String EMAIL = "test@example.com";

  @Test
  void testChangePassword() {
    PasswordChangeDto passwordChangeDto = new PasswordChangeDto("oldPassword", "newPassword");
    doNothing().when(operatorService).changePassword(EMAIL, passwordChangeDto);

    this.operatorResource.changePassword(EMAIL, passwordChangeDto);
    verify(operatorService).changePassword(EMAIL, passwordChangeDto);
  }

  @Test
  void testCreateUserOperator() {

    GrantedAuthority mockAuthority = () -> "ROLE_SUPPORT";
    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(mockAuthority);
    Authentication auth = new OperatorAuthentication(grantedAuthorities);

    SecurityContext ctx = mock(SecurityContext.class);
    when(ctx.getAuthentication()).thenReturn(auth);

    try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
      mocked.when(SecurityContextHolder::getContext).thenReturn(ctx);
      OperatorDto operatorDto = new OperatorDto(EMAIL, "Test",
          "User", "password", SystemRole.SUPPORT);
      doNothing().when(operatorService).createUser(operatorDto.toOperator(),operatorDto.getSystemRole());
      this.operatorResource.createUserOperator(operatorDto);
      verify(operatorService).createUser(operatorDto.toOperator(),operatorDto.getSystemRole());
    }
  }

  @Test
  void testGetOperatorInfoDto() {

    OperatorInfoDto operatorInfoDto = new OperatorInfoDto("Test", "User");
    when(operatorService.readGeneralInfo(EMAIL)).thenReturn(operatorInfoDto);

    final var actualResponse = this.operatorResource.getOperatorInfoDto(EMAIL);

    verify(operatorService).readGeneralInfo(EMAIL);
    Assertions.assertNotNull(actualResponse);
    Assertions.assertEquals(operatorInfoDto.getFirstName(), actualResponse.getFirstName());
    Assertions.assertEquals(operatorInfoDto.getFamilyName(), actualResponse.getFamilyName());

  }

  @Test
  void testLoginOperator() {
    String mockedToken = "mockedToken";
    when(operatorService.login(EMAIL)).thenReturn(mockedToken);
    User user = new User(EMAIL, "test", List.of());

    final var actualResponse = this.operatorResource.loginOperator(user);

    verify(operatorService).login(EMAIL);
    Assertions.assertNotNull(actualResponse);
    Assertions.assertEquals(mockedToken, actualResponse.getToken());
  }

  @Test
  void testUpdateGeneralInfo() {
    OperatorInfoDto operatorInfoDto = new OperatorInfoDto("Updated", "User");
    doNothing().when(operatorService).updateGeneralInfo(anyString(), any(OperatorInfoDto.class));

    this.operatorResource.updateGeneralInfo(EMAIL, operatorInfoDto);

    verify(operatorService).updateGeneralInfo(EMAIL, operatorInfoDto);
  }
}
