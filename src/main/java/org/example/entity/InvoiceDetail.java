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
}