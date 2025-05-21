package org.example.service.ServiceInterface;

import org.example.dto.ProductDetailDTO;
import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;

import java.util.List;

public interface InvoiceDetailService {
    List<InvoiceDetail> createInvoiceDetails(List<ProductDetailDTO> productDetails, Invoice invoice);
}