package org.example.repository;

import org.example.entity.Product;
import org.example.entity.ProductForSale;
import org.example.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductForSaleRepository extends JpaRepository<ProductForSale, Long> {
    List<ProductForSale> findByProduct(Product product);
    List<ProductForSale> findBySize(Size size);
    Optional<ProductForSale> findByProductAndSize(Product product, Size size);
    boolean existsByProductAndSize(Product product, Size size);
    List<ProductForSale> findByStockLessThan(int stock);
    boolean existsByProductIdAndSizeId(Long id, Long id1);
    List<ProductForSale> findByProductId(Long productId);
    List<ProductForSale> findBySizeId(Long sizeId);
}