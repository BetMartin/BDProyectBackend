package org.example.service.ServiceInterface;

import org.example.entity.Phone;

import java.util.List;

public interface PhoneService {
    Phone create(Phone phone);
    Phone findById(Long id);
    List<Phone> findAll();
    Phone update(Long id, Phone phone);
    void delete(Long id);
}