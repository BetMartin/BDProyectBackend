package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Province;
import org.example.repository.ProvinceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProvinciaService {
    
    private final ProvinceRepository provinciaRepository;

    @Transactional(readOnly = true)
    public List<Province> findAll() {
        return provinciaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Province> findById(Long id) {
        return provinciaRepository.findById(id);
    }

    public Province save(Province provincia) {
        return provinciaRepository.save(provincia);
    }

    public Province update(Long id, Province provincia) {
        if (!provinciaRepository.existsById(id)) {
            throw new RuntimeException("Provincia no encontrada con id: " + id);
        }
        provincia.setId(id);
        return provinciaRepository.save(provincia);
    }

    public void delete(Long id) {
        if (!provinciaRepository.existsById(id)) {
            throw new RuntimeException("Provincia no encontrada con id: " + id);
        }
        provinciaRepository.deleteById(id);
    }
}