package org.example.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ProductSizeDTO;
import org.example.service.SizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SizeController {

    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<List<ProductSizeDTO>> getAllSizes() {
        return ResponseEntity.ok(sizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> getSizeById(@PathVariable Long id) {
        return sizeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con id: " + id));
    }



}
//    @PostMapping
//    public ResponseEntity<ProductSizeDTO> createSize(@Valid @RequestBody ProductSizeDTO sizeDTO) {
//        return new ResponseEntity<>(sizeService.save(sizeDTO), HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductSizeDTO> updateSize(
//            @PathVariable Long id,
//            @Valid @RequestBody ProductSizeDTO sizeDTO) {
//        return ResponseEntity.ok(sizeService.update(id, sizeDTO));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
//        sizeService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
