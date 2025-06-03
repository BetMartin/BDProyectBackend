package org.example.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String status;
    private Long paymentId;
    private String message;
}
