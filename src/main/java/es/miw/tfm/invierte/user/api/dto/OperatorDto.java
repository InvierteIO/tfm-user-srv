package es.miw.tfm.invierte.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Data Transfer Object (DTO) for Operator.
 * This class is used to transfer operator-related data between
 * different layers of the application.
 * It includes validation annotations and utility methods for
 * converting to the Operator entity.
 *
 * @author denilssonmn
 * @author dev_castle
 */
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

  /**
   * Converts this DTO to an Operator entity.
   * It applies default values and encodes the password before conversion.
   *
   * @return the converted Operator entity
   */
  public Operator toOperator() {
    this.doDefault();
    Operator operator = new Operator();
    BeanUtils.copyProperties(this, operator);
    operator.setPassword(new BCryptPasswordEncoder().encode(this.password));
    return operator;
  }

  /**
   * Applies default values to the DTO fields if they are null.
   * Sets a random UUID as the password if it is null.
   * Sets the system role to SUPPORT if it is null.
   */
  public void doDefault() {
    if (Objects.isNull(password)) {
      password = UUID.randomUUID().toString();
    }
    if (Objects.isNull(systemRole)) {
      this.systemRole = SystemRole.SUPPORT;
    }
  }

}
