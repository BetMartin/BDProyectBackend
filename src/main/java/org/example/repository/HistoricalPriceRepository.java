package org.example.repository;

import org.example.entity.HistoricalPrice;
import org.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, Long> {

    List<HistoricalPrice> findByProductOrderByDateDesc(Product product);
    Optional<HistoricalPrice> findFirstByProductOrderByDateDesc(Product product);
    List<HistoricalPrice> findByDateBetween(LocalDate fechaInicio, LocalDate fechaFin);
}