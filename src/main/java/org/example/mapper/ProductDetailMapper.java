package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.ProductDetailDTO;
import org.example.entity.InvoiceDetail;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
    ProductStockMapper.class,
    OrderMapper.class
})
public interface ProductDetailMapper {

    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "productForSale", target = "productStock")
    @Mapping(target = "subtotal", expression = "java(invoiceDetail.getSubtotal())")
    @Mapping(target = "order",ignore = true)
    ProductDetailDTO toDto(InvoiceDetail invoiceDetail);

    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "productForSale", source = "productStock")
    @Mapping(target = "invoice", ignore = true)
    InvoiceDetail toEntity(ProductDetailDTO dto);

    List<ProductDetailDTO> toDtoList(List<InvoiceDetail> invoiceDetails);
    
    List<InvoiceDetail> toEntityList(List<ProductDetailDTO> dtos);
}