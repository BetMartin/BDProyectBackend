package org.example.dto;

import lombok.Data;

@Data
public class ProductStockDTO {
    private Long id;
    private int stock;
    private ProductDTO product;
    private ProductSizeDTO size;
}