package org.example.service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.mail.MessagingException;
import org.example.entity.Invoice;
import org.example.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Service
public class PaymentService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentClient paymentClient;

    @Transactional
    public Payment processPayment(Long invoiceId, String cardToken, String payerEmail) throws MPException, MPApiException {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoiceId));

        // Configurar el pago con MercadoPago
        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(invoice.getTotal()))
                .token(cardToken)
                .description("Compra en línea #" + invoice.getId())
                .installments(1)
                .payer(PaymentPayerRequest.builder()
                        .email(payerEmail)
                        .build())
                .build();

        Payment payment = paymentClient.create(paymentCreateRequest);

        if ("approved".equals(payment.getStatus())) {
            invoice.setPaymentId(String.valueOf(payment.getId()));
            invoice.setPaymentStatus("PAID");
            invoice.setPaymentDate(LocalDate.now());
            invoiceRepository.save(invoice);


            // Enviar factura por correo electrónico
            try {
                emailService.sendInvoiceEmail(invoice);
            } catch (MessagingException | IOException e) {
                // Loguear el error pero no interrumpir el flujo de la transacción
                System.err.println("Error al enviar la factura: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return payment;
    }
}
