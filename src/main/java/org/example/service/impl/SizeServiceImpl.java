package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Size;
import org.example.Repository.SizeRepository;
import org.example.service.ServiceInterface.SizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductSizeDTO> findAllDTO() {
        return sizeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSizeDTO findDTOById(Long id) {
        return sizeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con id: " + id));
    }

    @Override
    public ProductSizeDTO create(ProductSizeDTO productSizeDTO) {
        Size size = toEntity(productSizeDTO);
        return toDTO(sizeRepository.save(size));
    }

    @Override
    public ProductSizeDTO update(Long id, ProductSizeDTO productSizeDTO) {
        return sizeRepository.findById(id)
                .map(existingSize -> {
                    existingSize.setSizeNumber(productSizeDTO.getSize());
                    return toDTO(sizeRepository.save(existingSize));
                })
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!sizeRepository.existsById(id)) {
            throw new RuntimeException("Talla no encontrada con id: " + id);
        }
        sizeRepository.deleteById(id);
    }

    private ProductSizeDTO toDTO(Size size) {
        if (size == null) return null;
        
        return ProductSizeDTO.builder()
                .id(size.getId())
                .size(size.getSizeNumber())
                .build();
    }

    private Size toEntity(ProductSizeDTO dto) {
        if (dto == null) return null;
        
        return Size.builder()
                .id(dto.getId())
                .sizeNumber(dto.getSize())
                .build();
    }
}