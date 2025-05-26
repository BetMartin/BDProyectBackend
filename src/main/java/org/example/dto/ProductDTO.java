package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String product;
    private String brand;
    private String model;
    private String image;
    private String price;
    private String description;
    private ProductCategoryDTO category;
    private List<ProductSizeDTO> sizes;
    private List<ProductDetailDTO> ProductDetail;

}