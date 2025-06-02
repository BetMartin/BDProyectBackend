package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.OrderDTO;
import org.example.service.InvoiceDetailService;
import org.example.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final InvoiceService invoiceService;
    private final InvoiceDetailService invoiceDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = invoiceService.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = invoiceService.findById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(invoiceService.createInvoiceWithDetails(orderDTO), HttpStatus.CREATED);
    }
}













//    @PutMapping("/{id}")
//    public ResponseEntity<OrderDTO> updateOrder(
//            @PathVariable Long id,
//            @Valid @RequestBody OrderDTO orderDTO) {
//        OrderDTO updatedOrder = invoiceService.update(id, orderDTO);
//        return ResponseEntity.ok(updatedOrder);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//        invoiceService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

