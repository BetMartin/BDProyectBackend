package org.example.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private InvoiceService invoiceService;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@tuempresa.com");
        message.setTo(to);
        message.setSubject("Restablecimiento de contraseña");
        message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace: "
                + "http://localhost:5173/reset-password?token=" + token);

        mailSender.send(message);
    }
    public void sendInvoiceEmail(Invoice invoice) throws MessagingException, IOException {
        // Generar el PDF de la factura
        byte[] pdfBytes = invoiceService.generateInvoicePdf(invoice);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("elbuensabortrabajo@gmail.com");
        helper.setTo(invoice.getPerson().getUser().getUsername());
        helper.setSubject("Factura de tu compra #" + invoice.getId());

        String content =
                "<html><body>" +
                        "<h2>¡Gracias por tu compra!</h2>" +
                        "<p>Estimado/a " + invoice.getPerson().getFirstName() + ",</p>" +
                        "<p>Adjunto encontrarás la factura correspondiente a tu compra reciente.</p>" +
                        "<p>Detalles de la compra:</p>" +
                        "<ul>" +
                        "<li>Número de factura: " + invoice.getId() + "</li>" +
                        "<li>Fecha: " + invoice.getDate() + "</li>" +
                        "<li>Total: $" + invoice.getTotal() + "</li>" +
                        "</ul>" +
                        "<p>Si tienes alguna pregunta sobre tu pedido, no dudes en contactarnos.</p>" +
                        "<p>¡Gracias por confiar en nosotros!</p>" +
                        "<p>Atentamente,<br/>El equipo de Tu Empresa</p>" +
                        "</body></html>";

        helper.setText(content, true);

        // Adjuntar el PDF
        helper.addAttachment("Factura-" + invoice.getId() + ".pdf",
                new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}
