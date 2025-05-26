package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductSizeDTO;
import org.example.entity.Size;
import org.example.mapper.SizeMapper;
import org.example.repository.SizeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeService {
    
    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    @Transactional(readOnly = true)
    public List<ProductSizeDTO> findAll() {
        return sizeMapper.toDtoList(sizeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<ProductSizeDTO> findById(Long id) {
        return sizeRepository.findById(id)
                .map(sizeMapper::toDto);
    }

    public ProductSizeDTO save(ProductSizeDTO sizeDTO) {
        validateSize(sizeDTO);
        
        // Verificar si ya existe un tamaño con el mismo número
        if (sizeRepository.existsBySizeNumber(sizeDTO.getSize())) {
            throw new IllegalArgumentException("Ya existe un tamaño con el número: " + sizeDTO.getSize());
        }

        Size size = sizeMapper.toEntity(sizeDTO);
        size.setSizeNumber(sizeDTO.getSize());
        size = sizeRepository.save(size);
        return sizeMapper.toDto(size);
    }

    public ProductSizeDTO update(Long id, ProductSizeDTO sizeDTO) {
        if (!sizeRepository.existsById(id)) {
            throw new RuntimeException("Tamaño no encontrado con id: " + id);
        }

        validateSize(sizeDTO);

        // Verificar si ya existe otro tamaño con el mismo número
        Optional<Size> existingSize = sizeRepository.findBySizeNumber(sizeDTO.getSize());
        if (existingSize.isPresent() && !existingSize.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro tamaño con el número: " + sizeDTO.getSize());
        }

        Size size = sizeMapper.toEntity(sizeDTO);
        size.setId(id);
        size.setSizeNumber(sizeDTO.getSize());
        size = sizeRepository.save(size);
        return sizeMapper.toDto(size);
    }

    public void delete(Long id) {
        if (!sizeRepository.existsById(id)) {
            throw new RuntimeException("Tamaño no encontrado con id: " + id);
        }
        
        sizeRepository.deleteById(id);
    }

    private void validateSize(ProductSizeDTO sizeDTO) {
        if (sizeDTO.getSize() <= 0) {
            throw new IllegalArgumentException("El número de tamaño debe ser mayor que 0");
        }

        if (sizeDTO.getSize() > 50) {
            throw new IllegalArgumentException("El número de tamaño no puede ser mayor que 50");
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProductSizeDTO> findBySize(int size) {
        return sizeRepository.findBySizeNumber(size)
                .map(sizeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductSizeDTO> findBySizeGreaterThan(int size) {
        return sizeMapper.toDtoList(
                sizeRepository.findBySizeNumberGreaterThan(size)
        );
    }

    @Transactional(readOnly = true)
    public List<ProductSizeDTO> findBySizeLessThan(int size) {
        return sizeMapper.toDtoList(
                sizeRepository.findBySizeNumberLessThan(size)
        );
    }

    @Transactional(readOnly = true)
    public boolean existsBySize(int size) {
        return sizeRepository.existsBySizeNumber(size);
    }
}