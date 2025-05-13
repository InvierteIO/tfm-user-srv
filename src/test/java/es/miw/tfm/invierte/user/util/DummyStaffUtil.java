package es.miw.tfm.invierte.user.util;

import java.time.LocalDateTime;

import es.miw.tfm.invierte.user.api.dto.PasswordChangeDto;
import es.miw.tfm.invierte.user.api.dto.StaffDto;
import es.miw.tfm.invierte.user.api.dto.StaffInfoDto;
import es.miw.tfm.invierte.user.data.model.ActivationCode;
import es.miw.tfm.invierte.user.data.model.Staff;
import es.miw.tfm.invierte.user.data.model.enums.CompanyRole;
import es.miw.tfm.invierte.user.data.model.enums.Gender;
import es.miw.tfm.invierte.user.data.model.enums.Status;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DummyStaffUtil {
    public static final String TOKEN = "token";

    public static final String EMAIL = "email@email.com";

    public static final String NAME = "test";

    public static final String PASSWORD = "tempassword";

    public static final String TAX_IDENTIFICATION_NUMBER = "12345678A";

    public static final String ACTIVATION_CODE = "999999999999";

    public static Staff createRandomStaff(Status status) {
        Staff staff = new Staff();
        staff.setFirstName(NAME);
        staff.setFamilyName("Temp1");
        staff.setEmail(EMAIL);
        staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
        staff.setCompanyRole(CompanyRole.OWNER);
        staff.setStatus(status);
        return staff;
    }

    public static Staff createRandomActiveStaff() {
        Staff staff = new Staff();
        staff.setFirstName(NAME);
        staff.setFamilyName("Temp1");
        staff.setEmail(EMAIL);
        staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
        staff.setCompanyRole(CompanyRole.OWNER);
        staff.setStatus(Status.INACTIVE);
        return staff;
    }

    public static Staff createRandomInactiveStaffWithCompany() {
        Staff staff = new Staff();
        staff.setFirstName(NAME);
        staff.setFamilyName("Temp1");
        staff.setEmail(EMAIL);
        staff.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
        staff.setCompanyRole(CompanyRole.OWNER);
        staff.setTaxIdentificationNumber(TAX_IDENTIFICATION_NUMBER);
        staff.setStatus(Status.INACTIVE);

        ActivationCode activationCode = new ActivationCode();
        activationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        activationCode.setCode(ACTIVATION_CODE);
        staff.getActivationCodes().add(activationCode);

        return staff;
    }

    public static StaffInfoDto createRandomStaffInfoDto() {
        StaffInfoDto staff = new StaffInfoDto();
        staff.setFirstName(NAME);
        staff.setFamilyName("Temp1");
        staff.setGender(Gender.MALE);
        return staff;
    }

    public static PasswordChangeDto createRandomPasswordChangeDto() {
        return PasswordChangeDto.builder()
            .password(PASSWORD)
            .newPassword("passchange")
            .build();
    }

    public static StaffDto createRandomStaffDto() {
        return StaffDto.builder()
            .firstName("new")
            .familyName("new")
            .email("new@email.com")
            .password("newpass")
            .companyRole(CompanyRole.AGENT)
            .build();
    }

    public static Staff buildInactiveStaff() {
        Staff staff = new Staff();
        staff.setEmail(EMAIL);
        staff.setFirstName(NAME);
        staff.setCompanyRole(CompanyRole.AGENT);
        staff.setTaxIdentificationNumber("123456");
        staff.setStatus(Status.INACTIVE);
        return staff;
    }

    public static Staff buildActiveStaff() {
        Staff staff = new Staff();
        staff.setEmail(EMAIL);
        staff.setFirstName(NAME);
        staff.setCompanyRole(CompanyRole.AGENT);
        staff.setTaxIdentificationNumber("123456");
        staff.setStatus(Status.ACTIVE);
        return staff;
    }

    public static Staff buildInactiveStaffWithNoCompany() {
        Staff staff = new Staff();
        staff.setEmail(EMAIL);
        staff.setFirstName(NAME);
        staff.setCompanyRole(CompanyRole.AGENT);
        staff.setStatus(Status.INACTIVE);
        return staff;
    }

    public static Staff buildActiveStaffWithNoCompany() {
        Staff staff = new Staff();
        staff.setEmail(EMAIL);
        staff.setFirstName(NAME);
        staff.setCompanyRole(CompanyRole.AGENT);
        staff.setStatus(Status.ACTIVE);
        return staff;
    }

    public static Staff buildInactiveStaffWithCompany() {
        Staff staff = new Staff();
        staff.setEmail(EMAIL);
        staff.setFirstName(NAME);
        staff.setCompanyRole(CompanyRole.AGENT);
        staff.setStatus(Status.INACTIVE);
        staff.setTaxIdentificationNumber(TAX_IDENTIFICATION_NUMBER);
        return staff;
    }
}
