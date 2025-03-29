package es.miw.tfm.invierte.user.data.model.enums;

public enum SystemRole {
  ADMIN,
  SUPPORT;

  public static final String PREFIX = "ROLE_";

  public static SystemRole of(String withPrefix) {
    return SystemRole.valueOf(withPrefix.replace(SystemRole.PREFIX, ""));
  }
}
