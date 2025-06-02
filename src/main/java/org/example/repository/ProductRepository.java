package org.example.repository;

import org.example.entity.Product;
import org.example.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductCategoryId(Long categoryId);

    List<Product> findByProductCategoryName(String categoryName);

    List<Product> findByActivoTrue(); 
    
    @Query("SELECT c FROM ProductCategory c WHERE c.id = :categoryId")
    Optional<ProductCategory> findCategoryById(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.productCategory.name = :categoryName AND p.activo = true")
    List<Product> findActiveByProductCategoryName(@Param("categoryName") String categoryName);

}