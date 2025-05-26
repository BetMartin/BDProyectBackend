package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Person;
import org.example.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {
    
    private final PersonRepository personaRepository;

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personaRepository.findById(id);
    }

    public Person save(Person persona) {
        validatePersona(persona);
        return personaRepository.save(persona);
    }

    public Person update(Long id, Person persona) {
        if (!personaRepository.existsById(id)) {
            throw new RuntimeException("Persona no encontrada con id: " + id);
        }
        validatePersona(persona);
        persona.setId(id);
        return personaRepository.save(persona);
    }

    public void delete(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new RuntimeException("Persona no encontrada con id: " + id);
        }
        personaRepository.deleteById(id);
    }

    private void validatePersona(Person persona) {
        // Validar que el DNI no esté duplicado
        Optional<Person> existingPersona = personaRepository.findByDni(persona.getDni());
        if (existingPersona.isPresent() && !existingPersona.get().getId().equals(persona.getId())) {
            throw new IllegalArgumentException("Ya existe una persona con ese DNI");
        }
    }

}