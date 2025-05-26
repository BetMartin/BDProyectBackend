package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductStockDTO;
import org.example.entity.ProductForSale;
import org.example.mapper.ProductStockMapper;
import org.example.repository.ProductForSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductForSaleService {

    private final ProductForSaleRepository productForSaleRepository;
    private final ProductStockMapper productStockMapper;
    private final ProductStockService productStockService;

    @Transactional(readOnly = true)
    public List<ProductStockDTO> findAll() {
        return productStockMapper.toDtoList(productForSaleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<ProductStockDTO> findById(Long id) {
        return productForSaleRepository.findById(id)
                .map(productStockMapper::toDto);
    }

    public ProductStockDTO create(ProductStockDTO dto) {
        // Buscar si existe la combinación producto-talle
        Optional<ProductForSale> existingProductForSale = productForSaleRepository
                .findByProductIdAndSizeId(dto.getProduct().getId(), dto.getSize().getId());

        if (existingProductForSale.isPresent()) {
            // Si existe, registrar movimiento de stock
            ProductForSale productForSale = existingProductForSale.get();

            // Obtener el stock actual
            Integer currentStock = productStockService.getLastStockByProductForSaleId(productForSale.getId());

            // Calcular el nuevo stock (sumando el valor actual con el nuevo)
            int newStockValue = (currentStock != null ? currentStock : 0) + dto.getStock();

            // Registrar el nuevo movimiento con el stock actualizado
            if (newStockValue >= 0) {
                productStockService.registerStockMovement(productForSale, newStockValue);
            }

            return productStockMapper.toDto(productForSale);

        } else {
            // Si no existe, crear nuevo ProductForSale y registrar stock inicial
            ProductForSale productForSale = productStockMapper.toEntity(dto);
            ProductForSale savedProductForSale = productForSaleRepository.save(productForSale);

            // Registrar stock inicial
            if (dto.getStock() >= 0) {
                productStockService.registerStockMovement(savedProductForSale, dto.getStock());
            }

            return productStockMapper.toDto(savedProductForSale);
        }
    }


    public ProductStockDTO update(Long id, ProductStockDTO dto) {
        ProductForSale existingProductForSale = productForSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductSize no encontrado con id: " + id));

        // Verificar si la nueva combinación ya existe (si se está cambiando)
        if (!existingProductForSale.getProduct().getId().equals(dto.getProduct().getId()) ||
            !existingProductForSale.getSize().getId().equals(dto.getSize().getId())) {
            if (productForSaleRepository.existsByProductIdAndSizeId(
                    dto.getProduct().getId(), 
                    dto.getSize().getId())) {
                throw new IllegalArgumentException("Ya existe esta combinación de producto y talle");
            }
        }

        ProductForSale productForSale = productStockMapper.toEntity(dto);
        productForSale.setId(id);
        return productStockMapper.toDto(productForSaleRepository.save(productForSale));
    }

    public void delete(Long id) {
        productForSaleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductStockDTO> findByProduct(Long productId) {
        return productStockMapper.toDtoList(
                productForSaleRepository.findByProductId(productId)
        );
    }

    @Transactional(readOnly = true)
    public List<ProductStockDTO> findBySize(Long sizeId) {
        return productStockMapper.toDtoList(
                productForSaleRepository.findBySizeId(sizeId)
        );
    }

    public ProductStockDTO updateStock(Long productForSaleId, int newStock) {
        ProductForSale productForSale = productForSaleRepository.findById(productForSaleId)
                .orElseThrow(() -> new RuntimeException("ProductSize no encontrado con id: " + productForSaleId));

        productStockService.registerStockMovement(productForSale, newStock);
        return productStockMapper.toDto(productForSale);
    }

    @Transactional(readOnly = true)
    public Integer getCurrentStock(Long productSizeId) {
        return productForSaleRepository.findById(productSizeId)
                .map(ProductForSale::stockActualProductSize)
                .orElseThrow(() -> new RuntimeException("ProductSize no encontrado con id: " + productSizeId));
    }

    @Transactional(readOnly = true)
    public List<ProductStockDTO> findLowStock(int threshold) {
        return productStockMapper.toDtoList(
                productForSaleRepository.findAll().stream()
                        .filter(ps -> ps.stockActualProductSize() < threshold)
                        .toList()
        );
    }
}