package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductCategoryDTO;
import org.example.entity.ProductCategory;
import org.example.mapper.ProductCategoryMapper;
import org.example.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCategoryService {
    
    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductCategoryDTO findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    public ProductCategoryDTO save(ProductCategoryDTO categoryDTO) {
        validateCategory(categoryDTO);
        
        // Verificar si ya existe una categoría con el mismo nombre
        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoryDTO.getName());
        }

        ProductCategory category = categoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }
        
        // Verificar si la categoría tiene productos asociados
        ProductCategory category = categoryRepository.findById(id).get();
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        
        categoryRepository.deleteById(id);
    }

    private void validateCategory(ProductCategoryDTO categoryDTO) {
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        if (categoryDTO.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre de la categoría no puede exceder los 50 caracteres");
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProductCategoryDTO> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findByNameContaining(String name) {
        return categoryMapper.toDtoList(
                categoryRepository.findByNameContainingIgnoreCase(name)
        );
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}