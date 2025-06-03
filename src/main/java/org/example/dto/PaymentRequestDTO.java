package org.example.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long orderId;
    private String cardToken;
    private String payerEmail;
}
