package org.example.repository;

import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {
    List<InvoiceDetail> findByInvoiceId(Long invoiceId);

    Integer findTotalQuantitySoldByProductId(Long productId);

    @Query("SELECT id.invoice FROM InvoiceDetail id WHERE id.productForSale.product.id = :productId")
    List<Invoice> findInvoicesByProductId(@Param("productId") Long productId);
}
