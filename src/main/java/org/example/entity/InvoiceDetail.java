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
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "productSize_id")
    private ProductSizes productSize;

    private Integer quantity;

    //Producto vendido
    public Product getProduct() {
        return productSize.getProduct();
    }

    //Cambiar stock de ProductSize
    public ProductStock actualizarStock() {
        ProductSizes productoTalle = this.getProductSize();
        Integer stockActual = productoTalle.stockActualProductSize();

        if (stockActual < this.quantity) {
            throw new IllegalStateException("Stock insuficiente para realizar la venta");
        }

        ProductStock nuevoStock = new ProductStock();
        nuevoStock.setProductSize(productoTalle);
        nuevoStock.setDate(this.getInvoice().getDate());
        nuevoStock.setStock(stockActual - this.quantity);

        return nuevoStock;
    }

    //obtener el subtotal
    public Double getSubtotal() {
        return this.getProduct().precioActual() * this.getQuantity();
    }

}