package org.example.service.ServiceInterface;

import org.example.entity.Person;

import java.util.List;

public interface PersonService {
    Person create(Person persona);
    Person findById(Long id);
    List<Person> findAll();
    Person update(Long id, Person persona);
    void delete(Long id);
}