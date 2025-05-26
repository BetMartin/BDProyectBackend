package org.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore
    private Person person;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceDetail> details;

    //Obtener total de factura
    public double getTotal() {
        return details.stream()
                .mapToDouble(InvoiceDetail::getSubtotal)
                .sum();
    }
}