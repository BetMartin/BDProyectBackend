package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.ProductStockDTO;
import org.example.entity.ProductForSale;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
    ProductMapper.class,
    SizeMapper.class
})
public interface ProductStockMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"), // Asignar el id directamente
            @Mapping(target = "stock", expression = "java(productForSale.stockActualProductSize())"), // Obtener stock desde el método
            @Mapping(source = "product", target = "product"), // Mapear el producto usando ProductMapper
            @Mapping(source = "size", target = "size") // Mapear el tamaño usando SizeMapper
    })
    ProductStockDTO toDto(ProductForSale productForSale);

    @Mappings({
            @Mapping(source = "id", target = "id"), // El id del DTO corresponde al id de ProductForSale
            @Mapping(target = "size", source = "size"), // Mapear el tamaño desde el DTO
            @Mapping(target = "product", source = "product"), // Mapear el producto desde el DTO
            @Mapping(target = "productStocks", ignore = true)
    })
    ProductForSale toEntity(ProductStockDTO dto); // Mapear de vuelta desde el DTO a la entidad
    List<ProductStockDTO> toDtoList(List<ProductForSale> productForSaleList);
    List<ProductForSale> toEntityList(List<ProductStockDTO> productStockDTOList);

}
