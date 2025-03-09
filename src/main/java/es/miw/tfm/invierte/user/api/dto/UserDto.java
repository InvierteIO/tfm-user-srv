package es.miw.tfm.invierte.user.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.miw.tfm.invierte.user.data.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

  private String firstName;

  private String familyName;

  private String email;

  private Boolean isActive;

  private LocalDateTime registrationDate;

  public static UserDto ofBasicInfo(User user) {
    return UserDto.builder()
        .firstName(user.getFirstName())
        .familyName(user.getFamilyName())
        .email(user.getEmail())
        .isActive(user.getActive())
        .registrationDate(user.getRegistrationDate())
        .build();
  }
}
