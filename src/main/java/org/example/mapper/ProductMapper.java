package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.ProductDTO;
import org.example.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
    ProductCategoryMapper.class,
    SizeMapper.class,
    ProductDetailMapper.class
})
public interface ProductMapper {

    @Mapping(source = "product", target = "product")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "model", target = "model")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "price", expression = "java(String.valueOf(product.precioActual()))")
    @Mapping(source = "productCategory", target = "category")
    @Mapping(expression = "java(product.getTallesDisponibles())", target = "sizes")
    @Mapping(expression = "java(product.getInvoiceAsociadas())", target = "productDetail")
    ProductDTO toDto(Product product);

    @Mapping(target = "product", source = "product")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "model", source = "model")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "productCategory", source = "category")
    @Mapping(target = "historicalPrices", ignore = true)
    Product toEntity(ProductDTO dto);

    List<ProductDTO> toDtoList(List<Product> products);
    
    List<Product> toEntityList(List<ProductDTO> dtos);
}