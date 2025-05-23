package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ProductStockDTO;
import org.example.service.ProductForSaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductStockController {

    private final ProductForSaleService productForSaleService;

    @GetMapping
    public ResponseEntity<List<ProductStockDTO>> getAllProductStocks() {
        List<ProductStockDTO> stocks = productForSaleService.findAll();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStockDTO> getProductStockById(@PathVariable Long id) {
        return productForSaleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado con id: " + id));
    }

    @PostMapping
    public ResponseEntity<ProductStockDTO> createProductStock(@Valid @RequestBody ProductStockDTO productStockDTO) {
        ProductStockDTO createdStock = productForSaleService.create(productStockDTO);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductStockDTO> updateProductStock(
            @PathVariable Long id,
            @Valid @RequestBody ProductStockDTO productStockDTO) {
        ProductStockDTO updatedStock = productForSaleService.update(id, productStockDTO);
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductStock(@PathVariable Long id) {
        productForSaleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}