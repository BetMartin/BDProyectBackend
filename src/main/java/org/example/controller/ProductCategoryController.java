package org.example.controller;

import org.example.dto.ProductCategoryDTO;
import org.example.service.ServiceInterface.ProductCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories") // URL base para las categorías de productos
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    // Crear una nueva categoría de producto
    @PostMapping
    public ResponseEntity<ProductCategoryDTO> create(@RequestBody ProductCategoryDTO productCategoryDTO) {
        ProductCategoryDTO createdCategory = productCategoryService.create(productCategoryDTO);
        return ResponseEntity.ok(createdCategory);
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryDTO> getById(@PathVariable Long id) {
        ProductCategoryDTO productCategoryDTO = productCategoryService.findById(id);
        return ResponseEntity.ok(productCategoryDTO);
    }

    // Obtener todas las categorías de productos
    @GetMapping
    public ResponseEntity<List<ProductCategoryDTO>> getAll() {
        List<ProductCategoryDTO> productCategories = productCategoryService.findAll();
        return ResponseEntity.ok(productCategories);
    }

    // Actualizar una categoría de producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryDTO> update(@PathVariable Long id, @RequestBody ProductCategoryDTO productCategoryDTO) {
        ProductCategoryDTO updatedCategory = productCategoryService.update(id, productCategoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    // Eliminar una categoría de producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}