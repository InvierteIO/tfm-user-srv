package es.miw.tfm.invierte.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Password Change.
 * This class is used to transfer password change data between different layers of the application.
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetDto {
  @NotBlank
  @NotNull
  private String newPassword;

  @NotBlank
  @NotNull
  private String notificationToken;
}
