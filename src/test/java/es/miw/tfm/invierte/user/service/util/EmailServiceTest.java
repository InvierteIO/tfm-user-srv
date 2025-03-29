package es.miw.tfm.invierte.user.service.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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

  @Test
  public void testSendEmail() {
    this.emailService.sendEmail("to", "subject", "body");
    verify(this.mailSender).send(any(SimpleMailMessage.class));
  }

}
