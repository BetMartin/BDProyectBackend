package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Phone;
import org.example.repository.PhoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneService {
    
    private final PhoneRepository phoneRepository;

    @Transactional(readOnly = true)
    public List<Phone> findAll() {
        return phoneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Phone> findById(Long id) {
        return phoneRepository.findById(id);
    }

    public Phone save(Phone phone) {
        validatePhoneNumber(phone.getNumber());
        return phoneRepository.save(phone);
    }

    public Phone update(Long id, Phone phone) {
        if (!phoneRepository.existsById(id)) {
            throw new RuntimeException("Teléfono no encontrado con id: " + id);
        }
        validatePhoneNumber(phone.getNumber());
        phone.setId(id);
        return phoneRepository.save(phone);
    }

    public void delete(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new RuntimeException("Teléfono no encontrado con id: " + id);
        }
        phoneRepository.deleteById(id);
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("El número de teléfono debe tener 10 dígitos");
        }
    }
}