package es.miw.tfm.invierte.user.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails.
 * This class provides functionality to send simple email messages using the configured
 * `JavaMailSender`.
 *
 * <p>Utilizes Spring's email support to send messages with specified recipients, subjects,
 * and bodies.
 *
 * @see org.springframework.mail.javamail.JavaMailSender
 * @see org.springframework.mail.SimpleMailMessage
 *
 * @author denilssonmn
 */
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  /**
   * Sends an email with the specified recipient, subject, and body.
   *
   * @param to the recipient's email address
   * @param subject the subject of the email
   * @param body the body content of the email
   */
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  }

}
