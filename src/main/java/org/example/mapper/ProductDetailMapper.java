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
    @Mapping(source = "productSize", target = "productStock")
    @Mapping(target = "subtotal", expression = "java(invoiceDetail.getSubtotal())")
    @Mapping(source = "invoice", target = "order")
    ProductDetailDTO toDto(InvoiceDetail invoiceDetail);

    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "productSize", source = "productStock")
    @Mapping(target = "invoice", source = "order")
    InvoiceDetail toEntity(ProductDetailDTO dto);

    List<ProductDetailDTO> toDtoList(List<InvoiceDetail> invoiceDetails);
    
    List<InvoiceDetail> toEntityList(List<ProductDetailDTO> dtos);
}