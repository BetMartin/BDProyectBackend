package org.example.service.ServiceInterface;

import org.example.dto.SizeDTO;
import org.example.entity.Size;

import java.util.List;

public interface SizeService {

    List<SizeDTO> findAllDTO();

    SizeDTO findDTOById(Long id);

    SizeDTO create(SizeDTO sizeDTO);

    SizeDTO update(Long id, SizeDTO sizeDTO);

    void delete(Long id);
}