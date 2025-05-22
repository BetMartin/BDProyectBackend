package org.example.controller;

import org.example.dto.ProductSizeDTO;
import org.example.service.ServiceInterface.SizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/sizes")
@CrossOrigin(origins = "http://localhost:5173")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    // Obtener todos los tamaños
    @GetMapping
    public ResponseEntity<List<ProductSizeDTO>> getAllSizes() {
        List<ProductSizeDTO> sizes = sizeService.findAllDTO();
        return ResponseEntity.ok(sizes);
    }

    // Obtener un tamaño por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> getSizeById(@PathVariable Long id) {
        ProductSizeDTO size = sizeService.findDTOById(id);
        return ResponseEntity.ok(size);
    }

    // Crear un nuevo tamaño
    @PostMapping
    public ResponseEntity<ProductSizeDTO> createSize(@RequestBody ProductSizeDTO productSizeDTO) {
        // Usar el método create del servicio
        ProductSizeDTO createdSize = sizeService.create(productSizeDTO);
        return ResponseEntity.ok(createdSize);
    }

    // Actualizar un tamaño existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> updateSize(@PathVariable Long id, @RequestBody ProductSizeDTO productSizeDTO) {
        // Usar el método update del servicio
        ProductSizeDTO updatedSize = sizeService.update(id, productSizeDTO);
        return ResponseEntity.ok(updatedSize);
    }

    // Eliminar un tamaño por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        // Usar el método delete del servicio
        sizeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}