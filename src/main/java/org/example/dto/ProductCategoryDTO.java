package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductCategoryDTO {
    private Long id;
    private String name;
    private List<ProductDTO> products;

}