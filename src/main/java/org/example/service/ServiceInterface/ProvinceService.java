package org.example.service.ServiceInterface;

import org.example.entity.Province;

import java.util.List;

public interface ProvinceService {
    Province create(Province province);
    Province findById(Long id);
    List<Province> findAll();
    Province update(Long id, Province province);
    void delete(Long id);
}