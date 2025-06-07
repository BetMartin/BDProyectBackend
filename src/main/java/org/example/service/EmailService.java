package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Value("${mail.base.url}")
    private String baseUrl;

    public void sendPasswordResetEmail(String email, String nombre, String token) {
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(fromEmail, fromName);
            helper.setSubject("Recuperación de contraseña");

            String htmlContent = createPasswordResetEmailTemplate(nombre, resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", email);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error al enviar email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email", e);
        }
    }

    public void sendWelcomeEmail(String email, String nombre) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(fromEmail, fromName);
            helper.setSubject("¡Bienvenido!");

            String htmlContent = createWelcomeEmailTemplate(nombre);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de bienvenida enviado a: {}", email);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error al enviar email de bienvenida: {}", e.getMessage());
        }
    }

    private String createPasswordResetEmailTemplate(String nombre, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body>
                <h1>Hola %s,</h1>
                <p>Haz clic en el siguiente enlace para recuperar tu contraseña:</p>
                <a href="%s">Recuperar contraseña</a>
            </body>
            </html>
            """.formatted(nombre, resetUrl);
    }

    private String createWelcomeEmailTemplate(String nombre) {
        return """
            <!DOCTYPE html>
            <html>
            <body>
                <h1>¡Bienvenido %s!</h1>
                <p>Gracias por registrarte.</p>
            </body>
            </html>
            """.formatted(nombre);
    }
}