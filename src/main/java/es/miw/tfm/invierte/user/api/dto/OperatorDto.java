package es.miw.tfm.invierte.user.api.dto;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperatorDto {

  @NotNull
  @NotBlank
  private String firstName;

  @NotNull
  @NotBlank
  private String familyName;

  @NotNull
  @NotBlank
  private String email;

  private String password;

  private SystemRole systemRole;

  public Operator toOperator() {
    this.doDefault();
    Operator operator = new Operator();
    BeanUtils.copyProperties(this, operator);
    operator.setPassword(new BCryptPasswordEncoder().encode(this.password));
    return operator;
  }

  public void doDefault() {
    if (Objects.isNull(password)) {
      password = UUID.randomUUID().toString();
    }
    if (Objects.isNull(systemRole)) {
      this.systemRole = SystemRole.SUPPORT;
    }
  }

}
