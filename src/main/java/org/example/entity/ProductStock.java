package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Comparator;

@Data
@Entity
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer stock;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "productSize_id")
    private ProductSizes productSize;


}