package es.miw.tfm.invierte.user.util;

import es.miw.tfm.invierte.user.api.dto.OperatorDto;
import es.miw.tfm.invierte.user.api.dto.OperatorInfoDto;
import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.data.model.Operator;
import es.miw.tfm.invierte.user.data.model.enums.SystemRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DummyOperatorUtil {
    public static final String PASSWORD = "tempassword";
    public static final String FIRST_NAME = "Temp";
    public static final String FAMILY_NAME = "Temp1";
    public static final String EMAIL = "email@email.com";

    public static Operator createRandomOperator() {
        Operator operator = new Operator();
        operator.setFirstName(FIRST_NAME);
        operator.setFamilyName(FAMILY_NAME);
        operator.setEmail("temp@email.com");
        operator.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
        operator.setSystemRole(SystemRole.ADMIN);
        return operator;
    }

    public static Operator createRandomOperatorSupport() {
        Operator operator = new Operator();
        operator.setEmail(EMAIL);
        operator.setFirstName(FIRST_NAME);
        operator.setFamilyName(FAMILY_NAME);
        operator.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
        operator.setSystemRole(SystemRole.SUPPORT);
        return operator;
    }

    public static OperatorDto createRandomOperatorDto() {
        return OperatorDto.builder()
        .firstName("new")
        .familyName("new")
        .email("new@email.com")
        .password("newpass")
        .systemRole(SystemRole.SUPPORT)
        .build();
    }

    public static OperatorInfoDto createRandomOperatorInfoDto() {
        return OperatorInfoDto.builder()
            .firstName(FIRST_NAME)
            .familyName(FAMILY_NAME)
            .build();
    }

    public static OperatorInfoDto createRandomOperatorInfoChanged() {
        OperatorInfoDto operator = new OperatorInfoDto();
        operator.setFirstName("first name changed");
        operator.setFamilyName("family name changed");
        return operator;
    }

    public static PasswordChangeDto createRandomPasswordChangeDto() {
        return PasswordChangeDto.builder()
                .password(PASSWORD)
                .newPassword("passchange")
                .build();
    }
}
