package org.example.repository;

import org.example.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    List<OrderStatus> findByPedido_IdOrderByFechaDesc(Long idPedido);
}
