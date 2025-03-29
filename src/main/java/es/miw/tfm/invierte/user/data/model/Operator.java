package es.miw.tfm.invierte.user.data.model;

import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "operator_profile")
public class Operator extends User {

  @Enumerated(EnumType.STRING)
  SystemRole systemRole;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
}
