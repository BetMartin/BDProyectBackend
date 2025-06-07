package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String estado;
    private Double monto;
    private String moneda;
    private Date fechaCreacion;
    private Date fechaActualizacion;
    private String preferenceId;
    private String paymentId;
    private String descripcion;
}