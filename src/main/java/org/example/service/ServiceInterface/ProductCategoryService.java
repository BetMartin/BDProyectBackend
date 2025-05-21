package org.example.service.ServiceInterface;

import org.example.dto.ProductCategoryDTO;

import java.util.List;

public interface ProductCategoryService {
    ProductCategoryDTO create(ProductCategoryDTO productCategoryDTO);
    ProductCategoryDTO findById(Long id);
    List<ProductCategoryDTO> findAll();
    ProductCategoryDTO update(Long id, ProductCategoryDTO productCategoryDTO);
    void delete(Long id);
}