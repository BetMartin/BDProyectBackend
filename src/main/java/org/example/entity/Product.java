package org.example.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.example.dto.OrderDTO;
import org.example.dto.ProductSizeDTO;
import org.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;

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
    private List<ProductForSale> productForSales;


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
        if (productForSales == null || productForSales.isEmpty()) {
            return 0;
        }

        return productForSales.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .mapToInt(InvoiceDetail::getQuantity)
                .sum();
    }

    public List<ProductSizeDTO> getTallesDisponibles() {
        if (productForSales == null || productForSales.isEmpty()) {
            return Collections.emptyList();
        }

        return productForSales.stream()
                .filter(productSize -> productSize.stockActualProductSize() > 0)
                .map(productForSale -> ProductSizeDTO.builder()
                        .id(productForSale.getSize().getId())
                        .size(productForSale.getSize().getSizeNumber())
                        .build())
                .collect(Collectors.toList());
    }

    //obtener lista de facturas asociadas

    public List<Invoice> getInvoicesAsociadas() {
        if (productForSales == null || productForSales.isEmpty()) {
            return Collections.emptyList();
        }

        return productForSales.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .map(InvoiceDetail::getInvoice)
                .distinct()
                .collect(Collectors.toList());
    }

    //obtener lista de factura detalle asociada
    public List<InvoiceDetail> getInvoiceDetailsAsociadas() {
        if (productForSales == null || productForSales.isEmpty()) {
            return Collections.emptyList();
        }
        return productForSales.stream()
                .flatMap(productSize -> productSize.getInvoiceDetails().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}