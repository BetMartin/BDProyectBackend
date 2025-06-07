package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "order_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_pedido")
    private Long id;

    @Column(nullable = false)
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice pedido;

    // Constructor conveniente
    public OrderStatus(String estado, Invoice pedido) {
        this.estado = estado;
        this.fecha = new Date();
        this.pedido = pedido;
    }

    @Override
    public String toString() {
        return "EstadoPedido{" +
                "idEstadoPedido=" + id +
                ", estado='" + estado + '\'' +
                ", fecha=" + fecha +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                '}';
    }
}
