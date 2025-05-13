package es.miw.tfm.invierte.user.service.util;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith({MockitoExtension.class})
class EmailServiceTest {

  @InjectMocks
  private EmailService emailService;

  @Mock
  private JavaMailSender mailSender;

  public static final String TO = "to";

  public static final String SUBJECT = "subject";

  public static final String BODY = "body";

  @Test
  void testSendEmail() {
    this.emailService.sendEmail(TO, SUBJECT, BODY);
    verify(this.mailSender).send((SimpleMailMessage) argThat( message-> {
      final var messageCast = (SimpleMailMessage)message;
      Assertions.assertNotNull(messageCast.getTo());
      Assertions.assertEquals(TO, Objects.requireNonNull(messageCast.getTo()[0]));
      Assertions.assertEquals(SUBJECT, messageCast.getSubject());
      Assertions.assertEquals(BODY, messageCast.getText());
      return true;
    }));
  }

}
