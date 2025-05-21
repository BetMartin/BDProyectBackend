package org.example.service.impl;

import org.example.dto.ProductCategoryDTO;
import org.example.entity.ProductCategory;
import org.example.Repository.ProductCategoryRepository;
import org.example.service.ServiceInterface.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public ProductCategoryDTO create(ProductCategoryDTO productCategoryDTO) {
        ProductCategory productCategory = mapToEntity(productCategoryDTO);
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
        return mapToDTO(savedProductCategory);
    }

    @Override
    public ProductCategoryDTO findById(Long id) {
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductCategory no encontrada con ID: " + id));
        return mapToDTO(productCategory);
    }

    @Override
    public List<ProductCategoryDTO> findAll() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        return productCategories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryDTO update(Long id, ProductCategoryDTO productCategoryDTO) {
        if (!productCategoryRepository.existsById(id)) {
            throw new RuntimeException("ProductCategory no encontrada con ID: " + id);
        }

        ProductCategory productCategory = mapToEntity(productCategoryDTO);
        productCategory.setId(id);
        ProductCategory updatedProductCategory = productCategoryRepository.save(productCategory);
        return mapToDTO(updatedProductCategory);
    }

    @Override
    public void delete(Long id) {
        if (!productCategoryRepository.existsById(id)) {
            throw new RuntimeException("ProductCategory no encontrada con ID: " + id);
        }
        productCategoryRepository.deleteById(id);
    }

    private ProductCategoryDTO mapToDTO(ProductCategory productCategory) {
        ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
        productCategoryDTO.setId(productCategory.getId());
        productCategoryDTO.setName(productCategory.getName());
        // Mapear la lista de productos si corresponde
        productCategoryDTO.setProducts(null); // Aquí realizarías un mapeo si se relacionan productos
        return productCategoryDTO;
    }

    private ProductCategory mapToEntity(ProductCategoryDTO productCategoryDTO) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(productCategoryDTO.getId());
        productCategory.setName(productCategoryDTO.getName());
        return productCategory;
    }
}