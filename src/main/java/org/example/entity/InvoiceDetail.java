package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Optional;

@Data
@Entity
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "productForSale")
    private ProductForSale productForSale;

    private Integer quantity;

    //Producto vendido
    public Product getProduct() {
        return productForSale.getProduct();
    }

    //Cambiar stock de ProductSize
    public ProductStock actualizarStock() {
        ProductForSale productForSale = this.getProductForSale();
        Integer stockActual = productForSale.stockActualProductSize();

        System.out.println("==> Buscando ProductForSale con los siguientes datos:");
        System.out.println("Producto ID: " + productForSale.getProduct().getId());
        System.out.println("Talle ID: " + productForSale.getSize().getId());

        // Log para verificar el stock actual y la cantidad solicitada
        System.out.println("==> Verificando Stock");
        System.out.println("Producto: " + productForSale.getProduct().getId());
        System.out.println("Talle: " + productForSale.getSize().getId());
        System.out.println("ProductForSale: " + productForSale.getId());
        System.out.println("Stock Actual: " + stockActual);
        System.out.println("Cantidad Solicitada: " + this.quantity);


        if (stockActual < this.quantity) {
            System.out.println("** Error: Stock insuficiente. No se puede procesar la venta. **");
            throw new IllegalStateException("Stock insuficiente para realizar la venta");
        }

        // Log para verificar el nuevo stock que se establecerá
        System.out.println("==> Actualizando Stock");
        System.out.println("Nuevo Stock: " + (stockActual - this.quantity));

        ProductStock nuevoStock = new ProductStock();
        nuevoStock.setProductForSale(productForSale);
        nuevoStock.setDate(this.getInvoice().getDate());
        nuevoStock.setStock(stockActual - this.quantity);

        return nuevoStock;
    }

    //obtener el subtotal
    public Double getSubtotal() {
        return this.getProduct().precioActual() * this.getQuantity();
    }

}