package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
@Entity
public class ProductSizes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productSize")
    private List<InvoiceDetail> invoiceDetails;

    @OneToMany(mappedBy = "productSize")
    private List<ProductStock> productStocks;

    //Buscar stock actual
    public Integer stockActualProductSize() {
        if (productStocks == null || productStocks.isEmpty()) {
            return 0;
        }
        return productStocks.stream()
                .max(Comparator.comparing(ProductStock::getDate))
                .map(ProductStock::getStock)
                .orElse(0);
    }
}