package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.dto.ProductDetailDTO;
import org.example.entity.*;
import org.example.mapper.OrderMapper;
import org.example.repository.InvoiceRepository;
import org.example.repository.OrderStatusRepository;
import org.example.repository.ProductForSaleRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.entity.Invoice;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailService invoiceDetailService;
    private final ProductForSaleRepository productForSaleRepository;
    private final ProductStockService productStockService;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ProductService productService;

    // Buscar pedido por ID
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        // Utiliza el metodo personalizado que incluye FETCH para cargar los detalles
        List<Invoice> invoices = invoiceRepository.findAllWithDetails();
        return orderMapper.toDtoList(invoices);
    }

    @Transactional(readOnly = true)
    public OrderDTO findByIdDTO(Long id) {
        return invoiceRepository.findByIdWithDetails(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));

    }

    @Transactional
    public OrderDTO createInvoiceWithDetails(OrderDTO invoiceDTO) {

        // Buscar el usuario
        User user = userRepository.findById(invoiceDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + invoiceDTO.getUser().getId()));


        // Crear instancia de Invoice
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.parse(invoiceDTO.getFecha()));
        invoice.setPerson(user.getPerson());

        invoice.setDetails(new ArrayList<>());

        // Procesar cada detalle del JSON
        for (ProductDetailDTO detailDTO : invoiceDTO.getDetalles()) {
            // Buscar combinación de product y size en ProductForSale
            Optional<ProductForSale> productForSaleOpt = productForSaleRepository
                    .findByProductIdAndSizeId(
                            detailDTO.getProductStock().getProduct().getId(),
                            detailDTO.getProductStock().getSize().getId()
                    );
            if (productForSaleOpt.isEmpty()) {
                throw new RuntimeException("No existe un registro de ProductForSale para el producto con ID: "
                        + detailDTO.getProductStock().getProduct().getId()
                        + " y talle con ID: " + detailDTO.getProductStock().getSize().getId());
            }

            ProductForSale productForSale = productForSaleOpt.get();

            // Validar si es posible crear el detalle (suficiente stock)
            int stockActual = productForSale.stockActualProductSize();
            if (detailDTO.getQuantity() > stockActual) {
                throw new RuntimeException("Stock insuficiente para el producto con ID: "
                        + detailDTO.getProductStock().getProduct().getId()
                        + " y talle con ID: " + detailDTO.getProductStock().getSize().getId()
                        + ". Stock actual: " + stockActual + ", requerido: " + detailDTO.getQuantity());
            }

            // Crear y guardar el detalle
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(invoice);
            invoiceDetail.setProductForSale(productForSale);
            invoiceDetail.setQuantity(detailDTO.getQuantity());
            invoice.getDetails().add(invoiceDetail);

            // Actualizar el stock registrando el movimiento
            ProductStock nuevoStock = new ProductStock();
            nuevoStock.setProductForSale(productForSale);
            nuevoStock.setStock(stockActual - detailDTO.getQuantity());
            productStockService.save(nuevoStock);
        }

        // Guardar la factura
        invoiceRepository.save(invoice);

        return orderMapper.toDto(invoice);
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

    // Actualizar estado del pedido
    public Invoice actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {

        Invoice pedido = invoiceRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        // Crear nuevo registro de estado
        OrderStatus nuevoEstadoPedido = new OrderStatus(nuevoEstado, pedido);
        orderStatusRepository.save(nuevoEstadoPedido);

        // Si el pedido se cancela, devolver el stock
        if ("CANCELADO".equals(nuevoEstado)) {
            for (InvoiceDetail detalle : pedido.getDetails()) {
                ProductForSale productForSale = detalle.getProductForSale();
                Integer stockActual = productForSale.stockActualProductSize();

                // Crear nuevo registro de stock para devolver la cantidad
                ProductStock nuevoStock = new ProductStock();
                nuevoStock.setProductForSale(productForSale);
                nuevoStock.setDate(LocalDate.now());
                nuevoStock.setStock(stockActual + detalle.getQuantity());

                // Guardar el nuevo stock
                productStockService.save(nuevoStock);
            }
        }
        return pedido;
    }
}