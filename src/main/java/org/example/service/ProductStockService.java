package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.entity.ProductForSale;
import org.example.entity.ProductStock;
import org.example.repository.ProductForSaleRepository;
import org.example.repository.ProductStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductStockService {
    
    private final ProductStockRepository productStockRepository;
    private final ProductForSaleRepository productForSaleRepository;

    @Transactional(readOnly = true)
    public List<ProductStock> findAll() {
        return productStockRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ProductStock> findById(Long id) {
        return productStockRepository.findById(id);
    }

    public ProductStock save(ProductStock productStock) {
        validateProductStock(productStock);
        productStock.setDate(LocalDate.now());
        return productStockRepository.save(productStock);
    }

    public ProductStock update(Long id, ProductStock productStock) {
        if (!productStockRepository.existsById(id)) {
            throw new RuntimeException("Stock no encontrado con id: " + id);
        }
        validateProductStock(productStock);
        productStock.setId(id);
        return productStockRepository.save(productStock);
    }

    public void delete(Long id) {
        if (!productStockRepository.existsById(id)) {
            throw new RuntimeException("Stock no encontrado con id: " + id);
        }
        productStockRepository.deleteById(id);
    }

    private void validateProductStock(ProductStock productStock) {
        if (productStock.getProductForSale() == null) {
            throw new IllegalArgumentException("El ProductSize es obligatorio");
        }
        if (productStock.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    @Transactional(readOnly = true)
    public List<ProductStock> findByProductSize(ProductForSale productForSale) {
        return productStockRepository.findByProductForSaleOrderByDateDesc(productForSale);
    }

    @Transactional(readOnly = true)
    public Optional<ProductStock> findLatestStockByProductSize(ProductForSale productForSale) {
        return productStockRepository.findFirstByProductForSaleOrderByDateDesc(productForSale);
    }


    public ProductStock registerStockMovement(ProductForSale productSize, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("El nuevo stock no puede ser negativo");
        }
        
        ProductStock productStock = ProductStock.builder()
                .productForSale(productSize)
                .stock(newStock)
                .date(LocalDate.now())
                .build();
        
        return productStockRepository.save(productStock);
    }


    @Transactional(readOnly = true)
    public List<ProductStock> findLowStockProducts(int threshold) {
        return productStockRepository.findByStockLessThanAndDateInLatestForEachProductSize(threshold);
    }

    public Integer getLastStockByProductForSaleId(Long productForSaleId) {
        return productStockRepository.findTopByProductForSaleIdOrderByIdDesc(productForSaleId)
                .map(ProductStock::getStock)
                .orElse(0);
    }

    public Integer getCurrentStockByProductAndSize(Long productId, Long sizeId) {
        return productForSaleRepository
                .findByProductIdAndSizeId(productId, sizeId)
                .flatMap(productForSale -> productStockRepository
                        .findTopByProductForSaleIdOrderByIdDesc(productForSale.getId()))
                .map(ProductStock::getStock)
                .orElse(0);
    }
}