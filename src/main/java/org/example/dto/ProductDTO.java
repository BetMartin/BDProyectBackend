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
    private Double price;
    private Integer quantitySold;
    private String description;
    private List<SizeDTO> size;
    private List<ProductDetailDTO> orderDetail;

}