package es.miw.tfm.invierte.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Data Transfer Object (DTO) for Staff.
 * This class is used to transfer staff-related data between different layers of the application.
 * It includes validation annotations and utility methods for converting to the Staff entity.
 *
 * @author denilssonmn
 * @author dev_castle
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffDto {

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

  private CompanyRole companyRole;

  private LocalDate birthDate;

  private String identityDocument;

  private String jobTitle;

  private String address;

  private String phone;

  private Gender gender;

  private String taxIdentificationNumber;

  /**
   * Converts this `StaffDto` object to a `Staff` entity.
   * Copies the properties from the DTO to the entity and encodes the password.
   *
   * @return a `Staff` entity with the properties of this DTO
   */
  public Staff toStaff() {
    Staff staff = new Staff();
    BeanUtils.copyProperties(this, staff);
    staff.setPassword(new BCryptPasswordEncoder().encode(this.password));
    return staff;
  }
}
