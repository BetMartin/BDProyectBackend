package org.example.mapper;

import org.example.dto.ProductDetailDTO;
import org.example.dto.ProductDTO;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductDetailMapper.class, ProductCategoryMapper.class})
public interface ProductMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "product", target = "product"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "model", target = "model"),
            @Mapping(source = "image", target = "image"),
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "price", expression = "java(String.valueOf(product.precioActual()))"),
            @Mapping(source = "productCategory", target = "category", ignore = true),
            @Mapping(target = "sizes", expression = "java(mapSizes(product))"),
            @Mapping(target = "productDetail", expression = "java(mapInvoiceDetails(product))"),
            @Mapping(source = "activo", target = "activo")
    })
    ProductDTO toDto(Product product);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "product", target = "product"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "model", target = "model"),
            @Mapping(source = "image", target = "image"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "category", target = "productCategory", ignore = true),
            @Mapping(target = "historicalPrices", ignore = true),
            @Mapping(target = "productForSales", ignore = true),
            @Mapping(target = "tallesDisponibles", ignore = true),
            @Mapping(target = "invoicesAsociadas", ignore = true),
            @Mapping(target = "invoiceDetailsAsociadas", ignore = true),
            @Mapping(source = "activo", target = "activo")
    })
    Product toEntity(ProductDTO dto);

    default List<ProductDTO> toDtoList(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    default List<Product> toEntityList(List<ProductDTO> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default List<ProductSizeDTO> mapSizes(Product product) {
        if (product.getTallesDisponibles() == null) {
            return Collections.emptyList();
        }
        return product.getTallesDisponibles();
    }

    default List<ProductDetailDTO> mapInvoiceDetails(Product product) {
        if (product.getInvoiceDetailsAsociadas() == null) {
            return Collections.emptyList();
        }
        return product.getInvoiceDetailsAsociadas().stream()
                .map(invoiceDetail -> {
                    ProductDetailDTO dto = new ProductDetailDTO();
                    dto.setId(invoiceDetail.getId());
                    dto.setQuantity(invoiceDetail.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
