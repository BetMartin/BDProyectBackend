package org.example.controller;

import org.example.dto.SizeDTO;
import org.example.service.ServiceInterface.SizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sizes")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    // Obtener todos los tamaños
    @GetMapping
    public ResponseEntity<List<SizeDTO>> getAllSizes() {
        List<SizeDTO> sizes = sizeService.findAllDTO();
        return ResponseEntity.ok(sizes);
    }

    // Obtener un tamaño por ID
    @GetMapping("/{id}")
    public ResponseEntity<SizeDTO> getSizeById(@PathVariable Long id) {
        SizeDTO size = sizeService.findDTOById(id);
        return ResponseEntity.ok(size);
    }

    // Crear un nuevo tamaño
    @PostMapping
    public ResponseEntity<SizeDTO> createSize(@RequestBody SizeDTO sizeDTO) {
        // Usar el método create del servicio
        SizeDTO createdSize = sizeService.create(sizeDTO);
        return ResponseEntity.ok(createdSize);
    }

    // Actualizar un tamaño existente
    @PutMapping("/{id}")
    public ResponseEntity<SizeDTO> updateSize(@PathVariable Long id, @RequestBody SizeDTO sizeDTO) {
        // Usar el método update del servicio
        SizeDTO updatedSize = sizeService.update(id, sizeDTO);
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