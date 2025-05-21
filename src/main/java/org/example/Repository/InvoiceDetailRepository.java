package org.example.Repository;

import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {

    @Query("SELECT SUM(id.quantity) FROM InvoiceDetail id WHERE id.productSize.product.id = :productId")
    Integer findTotalQuantitySoldByProductId(Long productId);

    @Query("SELECT DISTINCT id.invoice FROM InvoiceDetail id WHERE id.productSize.product.id = :productId")
    List<Invoice> findInvoicesByProductId(Long productId);
}
