package org.example.service.ServiceInterface;

import org.example.dto.OrderDTO;
import org.example.entity.Invoice;

import java.util.List;

public interface InvoiceService {
    Invoice saveInvoice(OrderDTO orderDTO);
    List<Invoice> findAll();
    Invoice findById(Long id);
    Invoice updateInvoice(Long id, OrderDTO orderDTO);
    void deleteInvoice(Long id);
}