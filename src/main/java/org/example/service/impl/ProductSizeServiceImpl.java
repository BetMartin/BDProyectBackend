package org.example.service.impl;

import org.example.entity.ProductSizes;
import org.example.Repository.ProductSizesRepository;
import org.example.service.ServiceInterface.ProductSizeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizesRepository productSizeRepository;

    public ProductSizeServiceImpl(ProductSizesRepository productSizeRepository) {
        this.productSizeRepository = productSizeRepository;
    }

    @Override
    public ProductSizes create(ProductSizes productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    public ProductSizes findById(Long id) {
        return productSizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductSize no encontrado con ID: " + id));
    }

    @Override
    public List<ProductSizes> findAll() {
        return productSizeRepository.findAll();
    }

    @Override
    public ProductSizes update(Long id, ProductSizes productSize) {
        if (!productSizeRepository.existsById(id)) {
            throw new RuntimeException("ProductSize no encontrado con ID: " + id);
        }
        productSize.setId(id);
        return productSizeRepository.save(productSize);
    }

    @Override
    public void delete(Long id) {
        if (!productSizeRepository.existsById(id)) {
            throw new RuntimeException("ProductSize no encontrado con ID: " + id);
        }
        productSizeRepository.deleteById(id);
    }
}