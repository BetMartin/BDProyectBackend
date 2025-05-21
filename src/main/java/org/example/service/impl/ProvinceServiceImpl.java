package org.example.service.impl;

import org.example.entity.Province;
import org.example.Repository.ProvinceRepository;
import org.example.service.ServiceInterface.ProvinceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @Override
    public Province create(Province province) {
        return provinceRepository.save(province);
    }

    @Override
    public Province findById(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada con ID: " + id));
    }

    @Override
    public List<Province> findAll() {
        return provinceRepository.findAll();
    }

    @Override
    public Province update(Long id, Province province) {
        if (!provinceRepository.existsById(id)) {
            throw new RuntimeException("Provincia no encontrada con ID: " + id);
        }
        province.setId(id);
        return provinceRepository.save(province);
    }

    @Override
    public void delete(Long id) {
        if (!provinceRepository.existsById(id)) {
            throw new RuntimeException("Provincia no encontrada con ID: " + id);
        }
        provinceRepository.deleteById(id);
    }
}