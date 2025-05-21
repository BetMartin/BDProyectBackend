package org.example.service.ServiceInterface;

import org.example.entity.ProductSizes;

import java.util.List;

public interface ProductSizeService {
    ProductSizes create(ProductSizes productSize);
    ProductSizes findById(Long id);
    List<ProductSizes> findAll();
    ProductSizes update(Long id, ProductSizes productSize);
    void delete(Long id);
}