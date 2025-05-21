package org.example.service.impl;

import org.example.entity.ProductStock;
import org.example.Repository.ProductStockRepository;
import org.example.service.ServiceInterface.ProductStockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductStockRepository productStockRepository;

    public ProductStockServiceImpl(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    @Override
    public ProductStock create(ProductStock productStock) {
        return productStockRepository.save(productStock);
    }

    @Override
    public ProductStock findById(Long id) {
        return productStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock de producto no encontrado con ID: " + id));
    }

    @Override
    public List<ProductStock> findAll() {
        return productStockRepository.findAll();
    }

    @Override
    public ProductStock update(Long id, ProductStock productStock) {
        if (!productStockRepository.existsById(id)) {
            throw new RuntimeException("Stock de producto no encontrado con ID: " + id);
        }
        productStock.setId(id);
        return productStockRepository.save(productStock);
    }

    @Override
    public void delete(Long id) {
        if (!productStockRepository.existsById(id)) {
            throw new RuntimeException("Stock de producto no encontrado con ID: " + id);
        }
        productStockRepository.deleteById(id);
    }
}