package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
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

        if (stockActual < this.quantity) {
            throw new IllegalStateException("Stock insuficiente para realizar la venta");
        }

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