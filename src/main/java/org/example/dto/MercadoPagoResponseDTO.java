package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MercadoPagoResponseDTO {
    private String preferenceId;
    private String initPoint;
    private String sandboxInitPoint;
}
