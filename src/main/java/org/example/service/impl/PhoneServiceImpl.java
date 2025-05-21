package org.example.service.impl;

import org.example.entity.Phone;
import org.example.Repository.PhoneRepository;
import org.example.service.ServiceInterface.PhoneService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneServiceImpl implements PhoneService {

    private final PhoneRepository phoneRepository;

    public PhoneServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public Phone create(Phone phone) {
        return phoneRepository.save(phone);
    }

    @Override
    public Phone findById(Long id) {
        return phoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teléfono no encontrado con ID: " + id));
    }

    @Override
    public List<Phone> findAll() {
        return phoneRepository.findAll();
    }

    @Override
    public Phone update(Long id, Phone phone) {
        if (!phoneRepository.existsById(id)) {
            throw new RuntimeException("Teléfono no encontrado con ID: " + id);
        }
        phone.setId(id); // Establece el ID antes de actualizar
        return phoneRepository.save(phone);
    }

    @Override
    public void delete(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new RuntimeException("Teléfono no encontrado con ID: " + id);
        }
        phoneRepository.deleteById(id);
    }
}