package org.example.Repository;

import org.example.entity.HistoricalPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HistoricalPriceRepository extends JpaRepository<HistoricalPrice, Long> {

    @Query("SELECT h FROM HistoricalPrice h WHERE h.product.id = :productId ORDER BY h.date DESC")
    Optional<HistoricalPrice> findLatestPriceByProductId(Long productId);

}