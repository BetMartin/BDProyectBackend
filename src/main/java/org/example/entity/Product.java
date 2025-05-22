package org.example.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "product")
    private List<ProductSizes> productSizes;

    //Metodo para obtener el precio actual
    public Double precioActual() {
        if (historicalPrices == null || historicalPrices.isEmpty()) {
            return null;
        }
        return historicalPrices.stream()
                .max(Comparator.comparing(HistoricalPrice::getDate))
                .map(HistoricalPrice::getPrice)
                .orElse(null);
    }

    //Metodo para obtener cantidad vendida
    public Integer getCantidadTotalVendida() {
        if (productSizes == null || productSizes.isEmpty()) {
            return 0;
        }

        return productSizes.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .mapToInt(InvoiceDetail::getQuantity)
                .sum();
    }

    //Obtener lista de talles disponibles
    public List<Size> getTallesDisponibles() {
        if (productSizes == null || productSizes.isEmpty()) {
            return Collections.emptyList();
        }

        return productSizes.stream()
                .filter(productSize -> productSize.stockActualProductSize() > 0)
                .map(ProductSizes::getSize)
                .collect(Collectors.toList());
    }

    //obtener lista de facturas asociadas
    public List<Invoice> getInvoicesAsociadas() {
        if (productSizes == null || productSizes.isEmpty()) {
            return Collections.emptyList();
        }

        return productSizes.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .map(InvoiceDetail::getInvoice)
                .distinct()
                .collect(Collectors.toList());
    }

    //obtener lista de factura detalle asociada
    public List<InvoiceDetail> getInvoiceDetailsAsociadas() {
        if (productSizes == null || productSizes.isEmpty()) {
            return Collections.emptyList();
        }
        return productSizes.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}