package org.example.mapper;

import org.example.dto.ProductDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.ProductDTO;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Product;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

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
            @Mapping(target = "price", expression = "java(String.valueOf(product.precioActual()))"), // Conversión Double -> String
            @Mapping(source = "productCategory", target = "category", ignore = true),
            @Mapping(target = "sizes", expression = "java(mapSizes(product))"), // Llama al método mapSizes
            @Mapping(target = "productDetail", expression = "java(mapInvoiceDetails(product))") // Llama al método mapInvoiceDetails
    })
    ProductDTO toDto(Product product);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "product", target = "product"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "model", target = "model"),
            @Mapping(source = "image", target = "image"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "category", target = "productCategory",ignore = true), // Relación inversa con ProductCategoryMapper
            @Mapping(target = "historicalPrices", ignore = true),
            @Mapping(target = "productForSales", ignore = true), // Campos ignorados
            @Mapping(target = "tallesDisponibles", ignore = true), // Ignorar propiedades de negocio adicionales
            @Mapping(target = "invoicesAsociadas", ignore = true),
            @Mapping(target = "invoiceDetailsAsociadas", ignore = true)
    })
    Product toEntity(ProductDTO dto);

    // Conversión de una lista de entidades a DTO
    default List<ProductDTO> toDtoList(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Conversión de una lista de DTO a entidades
    default List<Product> toEntityList(List<ProductDTO> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // Mapear lista de talles disponibles
    default List<ProductSizeDTO> mapSizes(Product product) {
        if (product.getTallesDisponibles() == null) {
            return Collections.emptyList();
        }
        return product.getTallesDisponibles();
    }

    // Mapear lista de detalles de facturas asociadas
    default List<ProductDetailDTO> mapInvoiceDetails(Product product) {
        if (product.getInvoiceDetailsAsociadas() == null) {
            return Collections.emptyList();
        }
        return product.getInvoiceDetailsAsociadas().stream()
                .map(invoiceDetail -> {
                    // Se usa un mapper externo o se convierte manualmente
                    ProductDetailDTO dto = new ProductDetailDTO();
                    dto.setId(invoiceDetail.getId());
                    dto.setQuantity(invoiceDetail.getQuantity());
                    // Mapear otros campos según sea necesario
                    return dto;
                })
                .collect(Collectors.toList());
    }
}