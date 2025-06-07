package org.example.controller;

import org.example.entity.Province;
import org.example.repository.ProvinceRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProvinceController {
    private final ProvinceRepository provinceRepository;

    public ProvinceController(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @GetMapping("/provinces")
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }
}
