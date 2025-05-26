package org.example.repository;
import org.example.entity.ProductForSale;
import org.example.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    List<ProductStock> findByProductForSaleOrderByDateDesc(ProductForSale productForSale);

    Optional<ProductStock> findFirstByProductForSaleOrderByDateDesc(ProductForSale productForSale);
    Optional<ProductStock> findTopByProductForSaleIdOrderByIdDesc(Long productForSaleId);

    @Query("SELECT ps FROM ProductStock ps WHERE ps.date IN " +
            "(SELECT MAX(ps2.date) FROM ProductStock ps2 GROUP BY ps2.productForSale) " +
            "AND ps.stock < :threshold")
    List<ProductStock> findByStockLessThanAndDateInLatestForEachProductSize(@Param("threshold") int threshold);
}