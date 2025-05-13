package es.miw.tfm.invierte.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Staff Information.
 * This class is used to transfer staff-related data between
 * different layers of the application.
 * It includes fields for personal and job-related information.
 *
 * @author denilssonmn
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffInfoDto {
  @NotNull
  @NotBlank
  private String firstName;

  @NotNull
  @NotBlank
  private String familyName;

  private LocalDate birthDate;

  private String identityDocument;

  private String jobTitle;

  private String address;

  private String phone;

  private Gender gender;

}
