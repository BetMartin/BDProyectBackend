package org.example.dto;

import lombok.Data;

@Data
public class ProductDetailDTO {
    private Long id;
    private int quantity;
    private ProductStockDTO ProductStock;
    private double subtotal;
    private OrderDTO order;
}