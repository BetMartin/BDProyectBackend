package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.dto.ProductDetailDTO;
import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.example.mapper.OrderMapper;
import org.example.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailService invoiceDetailService;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        return orderMapper.toDtoList(invoiceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> findById(Long id) {
        return invoiceRepository.findById(id)
                .map(orderMapper::toDto);
    }

    public OrderDTO create(OrderDTO orderDTO) {
        // Crear la factura principal
        Invoice invoice = orderMapper.toEntity(orderDTO);
        invoice.setDate(LocalDate.parse(orderDTO.getFecha(), DateTimeFormatter.ISO_DATE));
        invoice.setDetails(new ArrayList<>());
        
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Procesar los detalles
        if (orderDTO.getDetalles() != null && !orderDTO.getDetalles().isEmpty()) {
            for (ProductDetailDTO detailDTO : orderDTO.getDetalles()) {
                detailDTO.setOrder(orderMapper.toDto(savedInvoice));
                ProductDetailDTO savedDetail = invoiceDetailService.create(detailDTO);
            }
        }

        // Recargar la factura con los detalles
        Invoice finalInvoice = invoiceRepository.findById(savedInvoice.getId()).get();
        return orderMapper.toDto(finalInvoice);
    }

    public OrderDTO update(Long id, OrderDTO orderDTO) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));

        // Actualizar campos básicos
        Invoice invoice = orderMapper.toEntity(orderDTO);
        invoice.setId(id);
        invoice.setDate(LocalDate.parse(orderDTO.getFecha(), DateTimeFormatter.ISO_DATE));
        invoice.setPerson(existingInvoice.getPerson()); // Mantener la persona original

        // Actualizar o crear nuevos detalles
        if (orderDTO.getDetalles() != null) {
            List<Long> updatedDetailIds = new ArrayList<>();
            
            for (ProductDetailDTO detailDTO : orderDTO.getDetalles()) {
                if (detailDTO.getId() != null) {
                    // Actualizar detalle existente
                    invoiceDetailService.update(detailDTO.getId(), detailDTO);
                    updatedDetailIds.add(detailDTO.getId());
                } else {
                    // Crear nuevo detalle
                    detailDTO.setOrder(orderDTO);
                    ProductDetailDTO savedDetail = invoiceDetailService.create(detailDTO);
                    updatedDetailIds.add(savedDetail.getId());
                }
            }

            // Eliminar detalles que ya no están en la lista
            existingInvoice.getDetails().stream()
                    .filter(detail -> !updatedDetailIds.contains(detail.getId()))
                    .forEach(detail -> invoiceDetailService.delete(detail.getId()));
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return orderMapper.toDto(updatedInvoice);
    }

    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));

        // Eliminar primero los detalles
        if (invoice.getDetails() != null) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                invoiceDetailService.delete(detail.getId());
            }
        }

        invoiceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Double calculateTotal(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(Invoice::getTotal)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + invoiceId));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> !invoice.getDate().isBefore(startDate) && !invoice.getDate().isAfter(endDate))
                .map(orderMapper::toDto)
                .toList();
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice.getDate() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (invoice.getPerson() == null) {
            throw new IllegalArgumentException("La persona es obligatoria");
        }
        if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }
    }
}