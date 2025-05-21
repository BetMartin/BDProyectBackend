package org.example.controller;

import org.example.dto.OrderDTO;
import org.example.entity.Invoice;
import org.example.service.ServiceInterface.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final InvoiceService invoiceService;

    public OrderController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // Obtener todas las facturas
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllOrders() {
        List<Invoice> invoices = invoiceService.findAll();
        return ResponseEntity.ok(invoices);
    }

    // Obtener una factura por ID
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getOrderById(@PathVariable Long id) {
        Invoice invoice = invoiceService.findById(id);
        return ResponseEntity.ok(invoice);
    }

    // Crear una nueva factura
    @PostMapping
    public ResponseEntity<Invoice> createOrder(@RequestBody OrderDTO orderDTO) {
        Invoice createdInvoice = invoiceService.saveInvoice(orderDTO);
        return ResponseEntity.ok(createdInvoice);
    }

    // Actualizar una factura existente
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        Invoice updatedInvoice = invoiceService.updateInvoice(id, orderDTO);
        return ResponseEntity.ok(updatedInvoice);
    }

    // Eliminar una factura por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}