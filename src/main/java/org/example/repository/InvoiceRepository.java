package org.example.repository;

import org.example.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Invoice> findByPersonId(Long personId);
}