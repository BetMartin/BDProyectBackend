package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer stock;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "productForSale")
    private ProductForSale productForSale;

}