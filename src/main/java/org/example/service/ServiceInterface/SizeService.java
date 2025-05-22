package org.example.service.ServiceInterface;

import org.example.dto.ProductSizeDTO;

import java.util.List;

public interface SizeService {

    List<ProductSizeDTO> findAllDTO();

    ProductSizeDTO findDTOById(Long id);

    ProductSizeDTO create(ProductSizeDTO productSizeDTO);

    ProductSizeDTO update(Long id, ProductSizeDTO productSizeDTO);

    void delete(Long id);
}