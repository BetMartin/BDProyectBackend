package org.example.Repository;

import org.example.entity.ProductSizes;
import org.example.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductSizesRepository extends JpaRepository<ProductSizes, Long> {
    @Query("SELECT ps.size FROM ProductSizes ps WHERE ps.product.id = :productId")
    List<Size> findSizesByProductId(Long productId);
}