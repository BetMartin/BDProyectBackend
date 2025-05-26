package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Address;
import org.example.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdressService {
    
    private final AddressRepository domicilioRepository;

    @Transactional(readOnly = true)
    public List<Address> findAll() {
        return domicilioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Address> findById(Long id) {
        return domicilioRepository.findById(id);
    }

    public Address save(Address domicilio) {
        validateDomicilio(domicilio);
        return domicilioRepository.save(domicilio);
    }

    public Address update(Long id, Address domicilio) {
        if (!domicilioRepository.existsById(id)) {
            throw new RuntimeException("Domicilio no encontrado con id: " + id);
        }
        validateDomicilio(domicilio);
        return domicilioRepository.save(domicilio);
    }

    public void delete(Long id) {
        if (!domicilioRepository.existsById(id)) {
            throw new RuntimeException("Domicilio no encontrado con id: " + id);
        }
        domicilioRepository.deleteById(id);
    }

    private void validateDomicilio(Address domicilio) {
        if (domicilio.getStreet() == null || domicilio.getStreet().trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede estar vacía");
        }
        if (domicilio.getStreetNumber() == null || domicilio.getStreetNumber() <= 0) {
            throw new IllegalArgumentException("El número debe ser mayor que 0");
        }
        if (domicilio.getProvince() == null) {
            throw new IllegalArgumentException("La provincia es obligatoria");
        }
    }
}