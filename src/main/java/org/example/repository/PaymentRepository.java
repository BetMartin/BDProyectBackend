package org.example.repository;

import org.example.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPedido_Id(Long pedidoId);
    Optional<Payment> findByMercadoPagoPreferenceId(String preferenceId);
    Optional<Payment> findByMercadoPagoPaymentId(String paymentId);
}
