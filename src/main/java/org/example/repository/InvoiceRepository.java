package org.example.repository;

import org.example.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPersonId(Long personId);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.details WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.details")
    List<Invoice> findAllWithDetails();

    @Query("SELECT FUNCTION('YEAR', i.date) as año, " +
            "FUNCTION('MONTH', i.date) as mes, " +
            "COUNT(d) as cantidadVentas, " +
            "SUM(COALESCE((SELECT SUM(d.quantity * p.price) " +
            "              FROM InvoiceDetail d " +
            "              JOIN d.productForSale pfs " +
            "              JOIN pfs.product.historicalPrices p " +
            "              WHERE d.invoice = i " +
            "              AND p.date <= i.date " +
            "              AND NOT EXISTS (" +
            "                  SELECT 1 FROM HistoricalPrice p2 " +
            "                  WHERE p2.product = p.product " +
            "                  AND p2.date > p.date " +
            "                  AND p2.date <= i.date" +
            "              )), 0)) as totalVentas " +
            "FROM Invoice i LEFT JOIN i.details d " +
            "GROUP BY FUNCTION('YEAR', i.date), FUNCTION('MONTH', i.date) " +
            "ORDER BY FUNCTION('YEAR', i.date) DESC, FUNCTION('MONTH', i.date) DESC")
    List<Object[]> findVentasMensuales();

    @Query("SELECT p.product as nombre, SUM(d.quantity) as cantidad " +
            "FROM Invoice i " +
            "JOIN i.details d " +
            "JOIN d.productForSale pfs " +
            "JOIN pfs.product p " +
            "GROUP BY p.product " +
            "ORDER BY cantidad DESC")
    List<Object[]> findProductosMasVendidos();

    @Query("SELECT FUNCTION('YEAR', i.date) as año, " +
            "FUNCTION('MONTH', i.date) as mes, " +
            "COUNT(d) as cantidadVentas, " +
            "SUM(COALESCE((SELECT SUM(d.quantity * p.price) " +
            "              FROM InvoiceDetail d " +
            "              JOIN d.productForSale pfs " +
            "              JOIN pfs.product.historicalPrices p " +
            "              WHERE d.invoice = i " +
            "              AND p.date <= i.date " +
            "              AND NOT EXISTS (" +
            "                  SELECT 1 FROM HistoricalPrice p2 " +
            "                  WHERE p2.product = p.product " +
            "                  AND p2.date > p.date " +
            "                  AND p2.date <= i.date" +
            "              )), 0)) as totalVentas " +
            "FROM Invoice i LEFT JOIN i.details d " +
            "WHERE i.date BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY FUNCTION('YEAR', i.date), FUNCTION('MONTH', i.date) " +
            "ORDER BY FUNCTION('YEAR', i.date) DESC, FUNCTION('MONTH', i.date) DESC")
    List<Object[]> findVentasMensualesPorFecha(@Param("fechaInicio") LocalDate fechaInicio,@Param("fechaFin") LocalDate fechaFin);

}