package es.miw.tfm.invierte.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for Token.
 * This class is used to transfer token-related data between
 * different layers of the application.
 * It includes fields for storing the token value.
 *
 * @author denilssonmn
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenDto {

  private String token;
}
