package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String fecha;
    private double total;
    private List<ProductDetailDTO> detalles;

}