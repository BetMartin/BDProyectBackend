package org.example.controller;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.mail.MessagingException;
import org.example.entity.Invoice;
import org.example.repository.InvoiceRepository;
import org.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("api/payments")
public class PaymentController {
    @Autowired
    private InvoiceRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentClient paymentClient;

    public Payment processPayment(Invoice order, String cardToken, String payerEmail) throws MPException, MPApiException {
        // Configurar el pago con MercadoPago
        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(new BigDecimal(order.getTotal()))
                .token(cardToken)
                .description("Compra en línea #" + order.getId())
                .installments(1)
                .payer(PaymentPayerRequest.builder()
                        .email(payerEmail)
                        .build())
                .build();

        Payment payment = paymentClient.create(paymentCreateRequest);

        if ("approved".equals(payment.getStatus())) {
            // Actualizar la orden con la información del pago
            order.setPaymentId(String.valueOf(payment.getId()));
            order.setPaymentStatus("PAID");
            order.setPaymentDate(LocalDate.now());
            orderRepository.save(order);

            // Enviar factura por correo electrónico
            try {
                emailService.sendInvoiceEmail(order);
            } catch (MessagingException | IOException e) {
                // Loguear el error pero no interrumpir el flujo de la transacción
                System.err.println("Error al enviar la factura: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return payment;
    }
}
