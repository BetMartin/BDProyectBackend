package org.example.repository;

import org.example.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByNameIgnoreCase(String name);
    List<ProductCategory> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
