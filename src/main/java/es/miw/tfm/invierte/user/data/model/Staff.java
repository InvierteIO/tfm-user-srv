package es.miw.tfm.invierte.user.data.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "staff_profile")
public class Staff extends User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private LocalDate birthDate;

  private String identityDocument;

  private String jobTitle;

  private String address;

  private String phone;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String taxIdentificationNumber;

  @Enumerated(EnumType.STRING)
  private CompanyRole companyRole;

  @OneToMany(targetEntity = ActivationCode.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "staffId", referencedColumnName = "id")
  private List<ActivationCode> activationCodes = new ArrayList<>();

  public void setDefaultNoCompany() {
    this.status = Status.INACTIVE;
    this.companyRole = CompanyRole.OWNER;
    this.taxIdentificationNumber = null;
  }

}
