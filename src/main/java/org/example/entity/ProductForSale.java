package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
@Entity
public class ProductForSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productForSale")
    private List<InvoiceDetail> invoiceDetails;

    @OneToMany(mappedBy = "productForSale")
    private List<ProductStock> productStocks;

    //Buscar stock actual
    public Integer stockActualProductSize() {
        if (productStocks == null || productStocks.isEmpty()) {
            System.out.println("==> No hay registros de stock. Devolviendo 0.");
            return 0;
        }

        System.out.println("==> Verificando lista de stocks...");
        productStocks.forEach(stock -> System.out.println("Fecha: " + stock.getDate() + ", Stock: " + stock.getStock()));

        return productStocks.stream()
                .max(Comparator.comparing(ProductStock::getDate))
                .map(stock -> {
                    System.out.println("==> Último stock encontrado: Fecha: " + stock.getDate() + ", Stock: " + stock.getStock());
                    return stock.getStock();
                })
                .orElseGet(() -> {
                    System.out.println("** Error: No se encontraron registros válidos. Devolviendo 0. **");
                    return 0;
                });
    }
}