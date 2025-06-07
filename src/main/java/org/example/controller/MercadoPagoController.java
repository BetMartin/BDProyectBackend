package org.example.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.example.dto.MercadoPagoResponseDTO;
import org.example.dto.PaymentDTO;
import org.example.entity.Payment;
import org.example.service.InvoiceService;
import org.example.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin(origins = "http://localhost:5173")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/crear/{idPedido}")
    public ResponseEntity<?> crearPago(@PathVariable Long idPedido) {
        try {
            Map<String, String> datosPago = paymentService.crearPago(idPedido);

            MercadoPagoResponseDTO response = MercadoPagoResponseDTO.builder()
                    .preferenceId(datosPago.get("preference_id"))
                    .initPoint(datosPago.get("init_point"))
                    .sandboxInitPoint(datosPago.get("sandbox_init_point"))
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear pago: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (MPException | MPApiException e) {
            logger.error("Error de Mercado Pago: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al procesar el pago con Mercado Pago");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<PaymentDTO>> obtenerPagosPorPedido(@PathVariable Long idPedido) {
        try {
            List<Payment> pagos = paymentService.obtenerPagosPorPedido(idPedido);
            List<PaymentDTO> response = paymentService.mapToDTO(pagos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener pagos para el pedido {}: {}", idPedido, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhookMercadoPago(
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "preference_id", required = false) String preferenceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestBody(required = false) String body) {

        logger.info("Webhook recibido - Topic: {}, ID: {}, Preference: {}, Status: {}",
                topic, id, preferenceId, status);

        if (preferenceId != null && status != null) {
            paymentService.procesarNotificacion(preferenceId, status);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pagoId}")
    public ResponseEntity<?> obtenerPago(@PathVariable Long pagoId) {
        try {
            return paymentService.findById(pagoId)
                    .map(pago -> ResponseEntity.ok(paymentService.mapToDTO(pago)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al obtener pago {}: {}", pagoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}