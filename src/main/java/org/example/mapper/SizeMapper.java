package org.example.mapper;

import org.mapstruct.Mapper;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Size;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    
    ProductSizeDTO toDto(Size size);
    
    Size toEntity(ProductSizeDTO dto);
    
    List<ProductSizeDTO> toDtoList(List<Size> sizes);
    
    List<Size> toEntityList(List<ProductSizeDTO> dtos);
}