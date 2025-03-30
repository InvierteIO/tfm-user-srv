package es.miw.tfm.invierte.user.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
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

  public Staff toStaff() {
    Staff staff = new Staff();
    BeanUtils.copyProperties(this, staff);
    staff.setPassword(new BCryptPasswordEncoder().encode(this.password));
    return staff;
  }
}
