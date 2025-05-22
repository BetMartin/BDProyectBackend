package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.ProductStockDTO;
import org.example.entity.ProductSizes;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
    ProductMapper.class,
    SizeMapper.class
})
public interface ProductStockMapper {

    @Mapping(target = "stock", expression = "java(productSizes.stockActualProductSize())")
    @Mapping(source = "product", target = "product")
    @Mapping(source = "size", target = "size")
    ProductStockDTO toDto(ProductSizes productSizes);

    @Mapping(target = "invoiceDetails", ignore = true)
    @Mapping(target = "productStocks", ignore = true)
    @Mapping(source = "product", target = "product")
    @Mapping(source = "size", target = "size")
    ProductSizes toEntity(ProductStockDTO dto);

    List<ProductStockDTO> toDtoList(List<ProductSizes> productSizes);
    
    List<ProductSizes> toEntityList(List<ProductStockDTO> dtos);
}