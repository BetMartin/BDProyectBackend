package org.example.service.ServiceInterface;

import org.example.entity.ProductStock;

import java.util.List;

public interface ProductStockService {
    ProductStock create(ProductStock productStock);
    ProductStock findById(Long id);
    List<ProductStock> findAll();
    ProductStock update(Long id, ProductStock productStock);
    void delete(Long id);
}