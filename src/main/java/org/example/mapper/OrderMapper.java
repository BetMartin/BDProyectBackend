package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.OrderDTO;
import org.example.entity.Invoice;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
    ProductDetailMapper.class
})
public interface OrderMapper {

    @Mapping(source = "date", target = "fecha")
    @Mapping(target = "total", expression = "java(invoice.getTotal())")
    @Mapping(source = "details", target = "detalles", ignore = true)
    OrderDTO toDto(Invoice invoice);

    @Mapping(target = "date", source = "fecha")
    @Mapping(target = "details", source = "detalles", ignore = true)
    @Mapping(target = "person", ignore = true)
    Invoice toEntity(OrderDTO dto);

    List<OrderDTO> toDtoList(List<Invoice> invoices);
    
    List<Invoice> toEntityList(List<OrderDTO> dtos);
}