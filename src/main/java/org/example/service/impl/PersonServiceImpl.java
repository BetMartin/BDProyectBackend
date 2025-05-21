package org.example.service.impl;

import org.example.entity.Person;
import org.example.Repository.PersonRepository;
import org.example.service.ServiceInterface.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personaRepository;

    public PersonServiceImpl(PersonRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public Person create(Person persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Person findById(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + id));
    }

    @Override
    public List<Person> findAll() {
        return personaRepository.findAll();
    }

    @Override
    public Person update(Long id, Person persona) {
        if (!personaRepository.existsById(id)) {
            throw new RuntimeException("Persona no encontrada con ID: " + id);
        }
        persona.setId(id);
        return personaRepository.save(persona);
    }

    @Override
    public void delete(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new RuntimeException("Persona no encontrada con ID: " + id);
        }
        personaRepository.deleteById(id);
    }
}