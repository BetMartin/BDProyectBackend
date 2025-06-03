package org.example.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.example.dto.OrderDTO;
import org.example.service.InvoiceService;
import org.example.entity.PreferenceMP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoController {
    @Autowired
    private InvoiceService pedidoService;

    @PostMapping("/crear-preferencia/{idPedido}")
    public ResponseEntity<?> getPreferenciaIdMercadoPago(@PathVariable Long idOrder) {
        try {
            OrderDTO pedido = pedidoService.findById(idOrder);

            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
            }

            pedido.getTotal();

            MercadoPagoConfig.setAccessToken("APP_USR-5935710845811407-051518-d0bfb248902cd4e3c8d0738f587e902f-2435790313");

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .title("Pedido")
                    .description("Pedido realizado desde el carrito de compras")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(new BigDecimal(pedido.getTotal()))
                    .build();

            List<PreferenceItemRequest> items = Collections.singletonList(itemRequest);

            PreferenceBackUrlsRequest backURL = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/success")
                    .pending("http://localhost:5173/failure")
                    .failure("http://localhost:5173/pending")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backURL)
                    .build();

            PreferenceClient client = new PreferenceClient();

            Preference preference = client.create(preferenceRequest);
            PreferenceMP mpPreference = new PreferenceMP();
            mpPreference.setStatusCode(preference.getResponse().getStatusCode());
            mpPreference.setId(preference.getId());

            return ResponseEntity.ok(mpPreference);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
