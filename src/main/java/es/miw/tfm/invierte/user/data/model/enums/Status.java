package es.miw.tfm.invierte.user.data.model.enums;

public enum Status {
  ACTIVE,
  INACTIVE,
  DELETED;

  public static Status of(String status) {
    return Status.valueOf(status);
  }

}
