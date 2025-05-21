package org.example.controller;

import org.example.entity.Person;
import org.example.service.ServiceInterface.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final PersonService personaService;

    public PersonaController(PersonService personaService) {
        this.personaService = personaService;
    }

    // Obtener todas las personas
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersonas() {
        List<Person> personas = personaService.findAll();
        return ResponseEntity.ok(personas);
    }

    // Obtener una persona por ID
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonaById(@PathVariable Long id) {
        Person persona = personaService.findById(id);
        return ResponseEntity.ok(persona);
    }

    // Crear una nueva persona
    @PostMapping
    public ResponseEntity<Person> createPersona(@RequestBody Person person) {
        Person createdPersona = personaService.create(person);
        return ResponseEntity.ok(createdPersona);
    }

    // Actualizar una persona existente
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePersona(@PathVariable Long id, @RequestBody Person person) {
        Person updatedPersona = personaService.update(id, person);
        return ResponseEntity.ok(updatedPersona);
    }

    // Eliminar una persona por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersona(@PathVariable Long id) {
        personaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}