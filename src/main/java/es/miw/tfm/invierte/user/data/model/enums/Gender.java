package es.miw.tfm.invierte.user.data.model.enums;

public enum Gender {
  MALE,
  FEMALE;

  public static Status of(String status) {
    return Status.valueOf(status);
  }
}
