package org.example.repository;

import org.example.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Invoice> findByPersonId(Long personId);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.details WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.details")
    List<Invoice> findAllWithDetails();
}