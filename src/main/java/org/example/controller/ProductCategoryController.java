package org.example.controller;

import org.example.dto.ProductCategoryDTO;
import org.example.service.ProductCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productCategories")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }


    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryDTO> getById(@PathVariable Long id) {
        return productCategoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }

    // Obtener todas las categorías de productos
    @GetMapping
    public ResponseEntity<List<ProductCategoryDTO>> getAll() {
        List<ProductCategoryDTO> productCategories = productCategoryService.findAll();
        return ResponseEntity.ok(productCategories);
    }


    // Eliminar una categoría de producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}