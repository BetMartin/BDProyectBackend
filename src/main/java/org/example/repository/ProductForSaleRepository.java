package org.example.repository;


import org.example.entity.ProductForSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProductForSaleRepository extends JpaRepository<ProductForSale, Long> {

    boolean existsByProductIdAndSizeId(Long id, Long id1);
    List<ProductForSale> findByProductId(Long productId);
    List<ProductForSale> findBySizeId(Long sizeId);
    Optional<ProductForSale> findByProductIdAndSizeId(Long productId, Long sizeId);
}
