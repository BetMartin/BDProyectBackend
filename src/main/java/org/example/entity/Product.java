package org.example.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;
    private String brand;
    private String model;
    private String image;
    private String description;

    @ManyToOne
    @JoinColumn(name = "ProductCategory_id")
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricalPrice> historicalPrices;
}