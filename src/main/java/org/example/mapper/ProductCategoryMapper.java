package org.example.mapper;

import org.mapstruct.Mapper;
import org.example.dto.ProductCategoryDTO;
import org.example.entity.ProductCategory;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ProductCategoryMapper {
    
    ProductCategoryDTO toDto(ProductCategory category);
    
    ProductCategory toEntity(ProductCategoryDTO dto);
    
    List<ProductCategoryDTO> toDtoList(List<ProductCategory> categories);
    
    List<ProductCategory> toEntityList(List<ProductCategoryDTO> dtos);
}