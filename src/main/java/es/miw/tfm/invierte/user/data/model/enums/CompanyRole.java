package es.miw.tfm.invierte.user.data.model.enums;

public enum CompanyRole {
  OWNER,
  AGENT;

  public static final String PREFIX = "ROLE_";

  public static CompanyRole of(String withPrefix) {
    return CompanyRole.valueOf(withPrefix.replace(CompanyRole.PREFIX, ""));
  }

}
