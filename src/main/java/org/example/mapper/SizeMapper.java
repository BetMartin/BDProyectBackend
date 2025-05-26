package org.example.mapper;

import org.mapstruct.Mapper;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Size;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SizeMapper {

    @Mapping(source = "sizeNumber", target = "size")
    ProductSizeDTO toDto(Size size);

    @Mapping(source = "size", target = "sizeNumber")
    Size toEntity(ProductSizeDTO dto);
    
    List<ProductSizeDTO> toDtoList(List<Size> sizes);
    
    List<Size> toEntityList(List<ProductSizeDTO> dtos);
}