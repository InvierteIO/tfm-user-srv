package es.miw.tfm.invierte.user.data.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activation_code")
public class ActivationCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  String code;

  LocalDateTime expirationDate;

}
