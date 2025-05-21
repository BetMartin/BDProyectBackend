package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.SizeDTO;
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
    public List<SizeDTO> findAllDTO() {
        return sizeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SizeDTO findDTOById(Long id) {
        return sizeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Talla no encontrada con id: " + id));
    }

    @Override
    public SizeDTO create(SizeDTO sizeDTO) {
        Size size = toEntity(sizeDTO);
        return toDTO(sizeRepository.save(size));
    }

    @Override
    public SizeDTO update(Long id, SizeDTO sizeDTO) {
        return sizeRepository.findById(id)
                .map(existingSize -> {
                    existingSize.setSizeNumber(sizeDTO.getSize());
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

    private SizeDTO toDTO(Size size) {
        if (size == null) return null;
        
        return SizeDTO.builder()
                .id(size.getId())
                .size(size.getSizeNumber())
                .build();
    }

    private Size toEntity(SizeDTO dto) {
        if (dto == null) return null;
        
        return Size.builder()
                .id(dto.getId())
                .sizeNumber(dto.getSize())
                .build();
    }
}