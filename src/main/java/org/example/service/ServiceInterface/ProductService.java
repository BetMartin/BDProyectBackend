package org.example.service.ServiceInterface;

import org.example.dto.ProductDTO;
import org.example.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductDTO toDTO(Product product);
    List<ProductDTO> findAllDTO();
    ProductDTO findDTOById(Long id);
    ProductDTO create(ProductDTO productDTO);
    ProductDTO update(Long id, ProductDTO productDTO);
    void delete(Long id);
}