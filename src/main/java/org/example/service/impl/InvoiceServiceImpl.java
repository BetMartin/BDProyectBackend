package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.example.Repository.InvoiceRepository;
import org.example.service.ServiceInterface.InvoiceDetailService;
import org.example.service.ServiceInterface.InvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailService invoiceDetailService;

    @Override
    public Invoice saveInvoice(OrderDTO orderDTO) {
        // Crear la entidad Invoice a partir del DTO
        Invoice invoice = new Invoice();
        invoice.setId(orderDTO.getId());
        invoice.setDate(LocalDate.parse(orderDTO.getFecha()));

        // Crear y asociar los detalles de la factura
        List<InvoiceDetail> invoiceDetails = invoiceDetailService.createInvoiceDetails(orderDTO.getDetalles(), invoice);

        // Guardar la entidad Invoice junto con los detalles
        invoiceRepository.save(invoice);

        return invoice;
    }

    @Override
    public List<Invoice> findAll() {
        return List.of();
    }

    @Override
    public Invoice findById(Long id) {
        return null;
    }

    @Override
    public Invoice updateInvoice(Long id, OrderDTO orderDTO) {
        return null;
    }

    @Override
    public void deleteInvoice(Long id) {

    }
}