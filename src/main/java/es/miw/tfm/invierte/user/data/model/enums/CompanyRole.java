package es.miw.tfm.invierte.user.data.model.enums;

public enum CompanyRole {
  OWNER,
  REALTOR,
  ADMINISTRATIVE_ASSISTANT;

  public static final String PREFIX = "ROLE_";

  public static CompanyRole of(String withPrefix) {
    return CompanyRole.valueOf(withPrefix.replace(CompanyRole.PREFIX, ""));
  }

}
